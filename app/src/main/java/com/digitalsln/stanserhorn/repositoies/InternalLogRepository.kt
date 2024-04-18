package com.digitalsln.stanserhorn.repositoies

import android.util.Log
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.locale.dao.InternalSynchronizationLogDao
import com.digitalsln.stanserhorn.data.locale.entries.InternalSynchronizationLogEntry
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.tools.NetworkHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

class InternalLogRepository @Inject constructor(
    private val internalSynchronizationLogDao: InternalSynchronizationLogDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkHelper: NetworkHelper,
) {

    suspend fun putLog(success: Boolean) {
        val calendar = GregorianCalendar.getInstance(Locale.GERMANY)
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY).format(calendar.time)
        val time = SimpleDateFormat("HH:mm:ss", Locale.GERMANY).format(calendar.time)

        val internalSyncLog = InternalSynchronizationLogEntry(
            syncDate = date,
            syncTime = time,
            syncStatus = success,
        )

        internalSynchronizationLogDao.insert(internalSyncLog)
        checkCountEntitiesInDb()
    }

    private suspend fun checkCountEntitiesInDb() {
        val updateSyncLogCallCounter = internalSynchronizationLogDao.getCount()
        val nCallsBeforeCleaning = preferenceHelper.nSyncLogCallsBeforeCleaning.toIntOrNull() ?: -1
        if(nCallsBeforeCleaning in 1..<updateSyncLogCallCounter) {
            Logger.d("In InternalLogRepository.checkCountEntitiesInDb: Call counter reached $updateSyncLogCallCounter, cleaning sync log.")
            val nEntriesToKeep = preferenceHelper.nSyncLogEntriesToKeep.toIntOrNull() ?: -1
            if(nEntriesToKeep in 1..<updateSyncLogCallCounter) { cleanSyncLog(nEntriesToKeep) }
        }
    }

    private suspend fun cleanSyncLog(nEntriesToKeep: Int) {
        val itemsToRemove = internalSynchronizationLogDao.getAllIds().apply { subList(nEntriesToKeep, size) }.toMutableList()

        val lastSuccessfulId = getLatestSync()?.id
        if(itemsToRemove.contains(lastSuccessfulId)) {
            Logger.d("In InternalLogRepository: Last successful sync (id=$lastSuccessfulId) is among the the ones to be deleted. Removing it from list of ids to be removed.")
            itemsToRemove.remove(lastSuccessfulId)
        }
        internalSynchronizationLogDao.deleteByIds(itemsToRemove)
    }

    private suspend fun getLatestSync(): InternalSynchronizationLogEntry? {
        var latestDateTime: Date? = null
        var retval: InternalSynchronizationLogEntry? = null
        internalSynchronizationLogDao.getAllSuccess().forEach { successLog ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
            val syncDateTime: Date? = try {
                dateFormat.parse(successLog.syncDate + " " + successLog.syncTime)
            } catch (e: ParseException) {
                Logger.e("In InternalLogRepository: Could not parse date string '" + successLog.syncDate + " " + successLog.syncTime + "'.", e)
                return null
            }
            if(latestDateTime == null || syncDateTime?.after(latestDateTime) == true) {
                latestDateTime = syncDateTime
                retval = successLog
            }
        }

        return retval
    }

    suspend fun minutesSinceLastSuccessfulSync(): Double {
        val entry = getLatestSync() ?: return -1.0
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
        val syncDateTime = try {
            dateFormat.parse(entry.syncDate + " " + entry.syncTime)
        } catch (e: ParseException) {
            Logger.e("In InternalLogRepository: Could not parse date string '" + entry.syncDate + " " + entry.syncTime + "'.", e)
            return -1.0
        }


        val currentTime = Date()

        val diffInMillis = currentTime.time - syncDateTime.time
        val diffInMinutes = diffInMillis.toDouble() / (1000 * 60)

        return (diffInMinutes * 100.0).roundToInt() / 100.0
    }

    fun syncLogAsStream(): ByteArrayInputStream {
        return runBlocking {
            withContext(Dispatchers.IO) {
                val syncLogAsString = internalSynchronizationLogDao.getAll().joinToString("\r\n")
                ByteArrayInputStream(syncLogAsString.toByteArray())
            }
        }
    }

    suspend fun isLastSyncOutdated(): Boolean {
        val minutesSinceLastSyncBeforeWarning = preferenceHelper.minutesSinceLastSyncBeforeWarning.toDoubleOrNull() ?: -1.0
        return minutesSinceLastSuccessfulSync() > minutesSinceLastSyncBeforeWarning
    }

    suspend fun logfileUploadRequestedByServer(): Boolean {
        val RETURN_VALUE_IN_CASE_OF_ERROR = true
        val logfileRequestUrl = preferenceHelper.logFileListenerUrl
        val responseString = networkHelper.fetchDataFromURL(logfileRequestUrl) { it }

        if (responseString == null) {
//            Logger.e("In InternalLogRepository: Could not convert InputStream to String.")
            return RETURN_VALUE_IN_CASE_OF_ERROR
        }

        val serverResponseCode: Int
        try {
            val ZERO_WIDTH_NO_BREAK_SPACE = "\uFEFF"
            serverResponseCode = responseString.replace("\n", "")
                .replace("\t", "")
                .replace("\r", "")
                .replace(ZERO_WIDTH_NO_BREAK_SPACE, "").toInt()
        } catch (e: NumberFormatException) {
            Logger.e("In InternalLogRepository: Converting server " +
                    "response to integer failed ('${e.message}') when trying to check if logfile should be uploaded.")
            return RETURN_VALUE_IN_CASE_OF_ERROR
        }
        Logger.d("In InternalLogRepository: Server repsonded with '$serverResponseCode'.")

        return when {
            serverResponseCode == 0 -> false
            serverResponseCode < 0 -> {
                val newLogLevel = when (serverResponseCode) {
                    -1 -> Logger.LogLevel.OFF
                    -2 -> Logger.LogLevel.DEBUG
                    -3 -> Logger.LogLevel.INFO
                    -4 -> Logger.LogLevel.WARNING
                    -5 -> Logger.LogLevel.ERROR
                    -6 -> Logger.LogLevel.FATAL
                    else -> {
                        Logger.e("In InternalLogRepository: Server requested invalid log level ${-1 * serverResponseCode}, set log level to INFO.")
                        Logger.LogLevel.INFO
                    }
                }
                preferenceHelper.logLevel = newLogLevel.toString()
                false
            }
            else -> true
        }
    }

    suspend fun uploadLogFileRequest(deviceUID: String, rootStorage: File?) {
        try {
            val result = uploadLogFile(deviceUID, rootStorage)
            if (result) {
                Log.i("Logger", "Log file upload completed successfully.")
            } else {
//                Log.e("Logger", "Log file upload failed.")
            }
        } catch (e: Exception) {
//            Log.e("Logger", "Log file upload failed.", e)
        }
    }

    private suspend fun uploadLogFile(deviceUID: String, rootStorage: File?): Boolean = withContext(Dispatchers.IO) {
        val logFile = File(rootStorage, Logger.getFileName())

        if (!logFile.isFile) {
//            Logger.e("In InternalLogRepository: Could not open log file for uploading.")
            return@withContext false
        }

        val urlForLogfileUpload = preferenceHelper.logFileUploadUrl

        val dateString = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale.GERMANY).format(Date())
        val cabinNumber = preferenceHelper.cabinNumber
        val uploadFileName = "logfile_${deviceUID}_Cabin_${cabinNumber}_$dateString.txt"

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("uploaded_file", uploadFileName, logFile.asRequestBody("text/plain".toMediaTypeOrNull()))
            .build()

        var error = false

        try {
            val res = networkHelper.postToUrl(urlForLogfileUpload, requestBody) { true }
            if (res != true) error = true
        } catch (e: IOException) {
            error = true
            Logger.e("In InternalLogRepository: Uploading log file failed.", e)
        }

        !error
    }

}