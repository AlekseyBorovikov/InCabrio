package com.digitalsln.stanserhorn.tools

import android.content.Context
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.WifiConnectionState
import com.digitalsln.stanserhorn.repositoies.DailyMenuRepository
import com.digitalsln.stanserhorn.repositoies.InfoBoardRepository
import com.digitalsln.stanserhorn.repositoies.InternalLogRepository
import com.digitalsln.stanserhorn.repositoies.ReservationRepository
import com.digitalsln.stanserhorn.repositoies.TripLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DataUpdateManager @Inject constructor(
    private val context: Context,
    private val wifiStateChannel: WifiStateChannel,
    private val wifiManager: LocaleWifiManager,
    private val infoBoardRepository: InfoBoardRepository,
    private val dailyMenuRepository: DailyMenuRepository,
    private val tripLogRepository: TripLogRepository,
    private val reservationRepository: ReservationRepository,
    private val internalLogRepository: InternalLogRepository,
    private val preferenceHelper: PreferenceHelper,
) {

    companion object {
        private const val TAG = "DataUpdateManager"
    }

    private val mHandler = android.os.Handler(Looper.getMainLooper())

    private var executorService: ScheduledExecutorService? = null

    fun start() {
        if (executorService == null) {
            Logger.d("$TAG: start scheduler.")
            executorService = Executors.newScheduledThreadPool(1)
            val interval: Long = preferenceHelper.networkPollingInterval.toLong()

            executorService?.scheduleWithFixedDelay({

                Logger.d("$TAG: DataUpdateManager starting.")

                if(preferenceHelper.debug) {
                    mHandler.post {
                        Toast.makeText(context, "DataUpdateManager started", Toast.LENGTH_SHORT).show()
                    }
                }

                wifiManager.doOnCorrectWifiNetwork(
                    onSuccess = { updateDataFromServer() },
                    onError = {
                        CoroutineScope(Dispatchers.IO).launch {

                            Logger.d("$TAG: No network connection found, not synchronizing.")

                            if(internalLogRepository.isLastSyncOutdated()) {
                                wifiStateChannel.send(WifiConnectionState.LastSyncOutdated)
                            }

                        }
                    }
                )

            }, 0, interval, TimeUnit.SECONDS)
        }
    }

    private fun updateDataFromServer() {
        Log.d(TAG, "start update data action")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                wifiStateChannel.send(WifiConnectionState.DataIsLoading)
                var allSynchronizationsSucceeded = true
                if (tripLogRepository.synchronizeTripLog()) {
                    Logger.d("$TAG: Trip log uploaded.");
                    if (tripLogRepository.synchronizeTable()) {
                        Logger.d("$TAG: Trip log downloaded.")
                    } else {
                        allSynchronizationsSucceeded = false
                        Logger.w("$TAG: Could not download trip log.")
                    }
                    if (infoBoardRepository.synchronizeTable()) {
                        Logger.d("$TAG: Infoboard synchronized.");
                    } else {
                        allSynchronizationsSucceeded = false
                        Logger.w("$TAG: Could not synchronize infoboard.");
                    }
                    if (dailyMenuRepository.synchronizeTable()) {
                        Logger.d("$TAG: Daily menu synchronized.");
                    } else {
                        allSynchronizationsSucceeded = false
                        Logger.w("$TAG: Could not synchronize daily menu.");
                    }
                    if (reservationRepository.synchronizeTable()) {
                        Logger.d("$TAG: Reservations synchronized.");
                    } else {
                        allSynchronizationsSucceeded = false
                        Logger.w("$TAG: Could not synchronize reservations.");
                    }
                } else {
                    allSynchronizationsSucceeded = false
                    Logger.w("$TAG: Could not upload trip log. Will not attempt to download trip log to prevent data loss.")
                }

                if(internalLogRepository.logfileUploadRequestedByServer() || Logger.getInstance()?.needUploadToServer == true) {
                    val deviceUID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                    val rootStorage = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                    internalLogRepository.uploadLogFileRequest(deviceUID, rootStorage)
                }

                internalLogRepository.putLog(allSynchronizationsSucceeded)

                Log.d(TAG, "update data was finished")
                wifiStateChannel.send(WifiConnectionState.DataWasLoaded)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stop() {
        if (executorService != null) {
            executorService?.shutdown()
            executorService = null
        }
    }

    fun restart() {
        stop()
        start()
    }

}