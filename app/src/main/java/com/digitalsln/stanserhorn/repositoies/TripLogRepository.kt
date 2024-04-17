package com.digitalsln.stanserhorn.repositoies

import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.locale.dao.TripLogDao
import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry
import com.digitalsln.stanserhorn.data.mappers.TripLogMapper
import com.digitalsln.stanserhorn.data.remote.TripLogRemote
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.tools.NetworkHelper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import okhttp3.FormBody
import javax.inject.Inject

class TripLogRepository @Inject constructor(
    private val dataUpdateChannel: DataUpdateChannel,
    private val tripLogDao: TripLogDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkHelper: NetworkHelper,
) {

    companion object {
        private const val TAG = "TripLogRepository"
    }

    private val dummyTripLogIds = listOf(100000, 100001, 100002, 100003, 100004, 100005, 100006, 100007, 100008, 100009)

    suspend fun fillDummyTripLogList() {
        val dummyTripLogList = mutableListOf<TripLogEntry>()
        dummyTripLogIds.forEach {
            dummyTripLogList.add(
                TripLogEntry(
                    globeId = it,
                    internalId = 26,
                    deviceUID = "4687",
                    cabinNumber = 13,
                    tripOfDay = 301,
                    date = "2024-03-12",
                    time = "17:16",
                    numberPassengers = 25,
                    ascent = false,
                    remarks = "Talfahrt",
                    show = true,
                    updated = false,
                )
            )
        }
        tripLogDao.insert(dummyTripLogList)
        dataUpdateChannel.send(DataUpdateEvent.TripLogUpdated)
    }

    suspend fun removeDummyTripLogList() {
        tripLogDao.deleteByIds(dummyTripLogIds)
        dataUpdateChannel.send(DataUpdateEvent.TripLogUpdated)
    }

    suspend fun getAllTripLogList(): List<TripLogEntry> = tripLogDao.getAllShowItem(preferenceHelper.cabinNumber)

    suspend fun synchronizeTable(): Boolean {
        val url = preferenceHelper.tripLogDownloadUrl

        val tripLogRemote = networkHelper.fetchDataFromURL(url) {
            val res = XmlMapper().readValue(it, TripLogRemote::class.java)
            res
        } ?: return false

        // get all id from table
        val allEntities = tripLogDao.getAllItem()
        val allIds = allEntities.map { it.globeId ?: 0 }
        val listToRemove = allIds.toMutableList()

        return try {
            // update or insert new entries
            tripLogRemote.entries?.forEach { tripLogEntry ->
                val localEntry = TripLogMapper.mapFromRemote(tripLogEntry)
                listToRemove.removeIf { it == localEntry.globeId }
                allEntities.find { it.globeId == tripLogEntry.id }?.let { updateEntry ->
                    tripLogDao.update(
                        updateEntry.copy(
                            cabinNumber = localEntry.cabinNumber,
                            tripOfDay = localEntry.tripOfDay,
                            date = localEntry.date,
                            time = localEntry.time,
                            numberPassengers = localEntry.numberPassengers,
                            ascent = localEntry.ascent,
                            remarks = localEntry.remarks,
                            show = localEntry.show,
                            updated = localEntry.updated,
                        )
                    )
                } ?: tripLogDao.insert(localEntry)
            }
            // remove other entries
            tripLogDao.deleteByIds(listToRemove)
            dataUpdateChannel.send(DataUpdateEvent.TripLogUpdated)
            true
        } catch (e: Exception) {
            Logger.e("$TAG: Could not parse XML ('${e.message}').", e)
            false
        }
    }

    suspend fun createTripLog(
        deviceUID: String,
        tripOfDay: Int,
        date: String,
        time: String,
        passengers: Int,
        ascent: Boolean,
        remarks: String,
    ) {
        val tripLogEntry = TripLogEntry(
            globeId = null,
            internalId = 0,
            deviceUID = deviceUID,
            cabinNumber = preferenceHelper.cabinNumber.toIntOrNull() ?: 0,
            tripOfDay = tripOfDay,
            date = date,
            time = time,
            numberPassengers = passengers,
            ascent = ascent,
            remarks = remarks,
            show = TripLogMapper.show(date, time),
            updated = false,
        )
        tripLogDao.insert(tripLogEntry)
        dataUpdateChannel.send(DataUpdateEvent.TripLogUpdated)
    }

    suspend fun updateTripLog(
        internalId: Long,
        globeId: Int?,
        deviceUID: String,
        tripOfDay: Int,
        date: String,
        time: String,
        passengers: Int,
        ascent: Boolean,
        remarks: String,
    ) {
        val tripLogEntry = TripLogEntry(
            globeId = globeId,
            internalId = internalId,
            deviceUID = deviceUID,
            cabinNumber = preferenceHelper.cabinNumber.toIntOrNull() ?: 0,
            tripOfDay = tripOfDay,
            date = date,
            time = time,
            numberPassengers = passengers,
            ascent = ascent,
            remarks = remarks,
            show = TripLogMapper.show(date, time),
            updated = true,
        )
        tripLogDao.update(tripLogEntry)
        dataUpdateChannel.send(DataUpdateEvent.TripLogUpdated)
    }

    suspend fun synchronizeTripLog(): Boolean {
        val tripLogEntries = tripLogDao.getCreatedOrUpdated()
        if (tripLogEntries.isEmpty()){
            Logger.d("$TAG: No trips found to upload, returning without doing anything.")
            return true
        }

        var success = true

        // Iterate through a list of records
        tripLogEntries.forEach { tripLogEntry ->
            // Uploading a record to the server and getting its ID
            val globalId = uploadTripLog(tripLogEntry)

            // Verifying download success
            if (globalId == null) {
                success = false
                return@forEach
            }

            // If the entry was modified locally
            if (tripLogEntry.updated) {
                // Updating a record on the server
                if (globalId != tripLogEntry.globeId) {
                    Logger.e("$TAG: Mismatch between ID returned from server ('$globalId') and local ID ('${tripLogEntry.globeId}').")
                }

                // Resetting the update flag
                val updatedTripEntry = tripLogEntry.copy(updated = false)
                val count = tripLogDao.update(updatedTripEntry)

                // Verifying the success of the update
                if (count != 1) {
                    Logger.e("$TAG: Update affected more than one rows ($count) when resetting the update flag for trip log entry ${tripLogEntry.internalId}.")
                    success = false
                } else {
                    Logger.d("$TAG: Updated entry on the server $tripLogEntry (Entry was already uploaded but modified locally).")
                }
            } else {
                // If the entry was added locally
                // Assigning a global ID and adding an entry to the local database
                val updatedTripEntry = tripLogEntry.copy(globeId = globalId, updated = false)
                val count = tripLogDao.update(updatedTripEntry)

                // Checking whether the addition was successful
                if (count != 1) {
                    Logger.e("$TAG: Update affected more than one rows ($count) when setting the global id ('$globalId') for trip log entry ${tripLogEntry.internalId}.")
                    success = false
                } else {
                    Logger.d("$TAG: Uploaded entry to the server $tripLogEntry (Entry was newly inserted into the server's database).")
                }
            }
        }

        return success
    }

    private suspend fun uploadTripLog(tripLogEntry: TripLogEntry): Int? {
        Logger.d("$TAG: Uploading trip log entry $tripLogEntry.")

        val urlString: String = preferenceHelper.tripLogUploadUrl

        return networkHelper.postToUrl(
            urlString = urlString,
            requestBody = FormBody.Builder()
                .add("fahrt_interne_id", tripLogEntry.internalId.toString())
                .add("fahrt_globale_id", if (tripLogEntry.globeId == null) "" else tripLogEntry.globeId.toString() )
                .add("fahrt_updated", tripLogEntry.updated.toString())
                .add("fahrt_geraete_id", tripLogEntry.deviceUID)
                .add("fahrt_kabine", tripLogEntry.cabinNumber.toString())
                .add("fahrt_fahrtenzaehler_tag", tripLogEntry.tripOfDay.toString())
                .add("fahrt_datum", tripLogEntry.date)
                .add("fahrt_zeit", tripLogEntry.time)
                .add("fahrt_pax", tripLogEntry.numberPassengers.toString())
                .add("fahrt_bergfahrt", (if (tripLogEntry.ascent) 1 else 0).toString())
                .add("fahrt_bemerkungen", tripLogEntry.remarks)
                .build()
        ) { responseBody ->
            val responseBodyString = responseBody?.string()
            val globalIdFromServer = responseBodyString?.toIntOrNull()

            if (globalIdFromServer == null || globalIdFromServer == -1) {
                Logger.e("$TAG: Server returned global id -1 when trying to upload trip log entry '${tripLogEntry.internalId}'.")
                null
            } else {
                Logger.d("$TAG: Server returned global id '$globalIdFromServer'.")
                globalIdFromServer
            }
        }
    }

    suspend fun getTripOfDayFromDb() = tripLogDao.getTripLogsByShow(true, preferenceHelper.cabinNumber).firstOrNull()?.tripOfDay ?: 0
    suspend fun getLastDirection() = tripLogDao.getTripLogsSortedByDate(preferenceHelper.cabinNumber).firstOrNull()?.ascent ?: false

}