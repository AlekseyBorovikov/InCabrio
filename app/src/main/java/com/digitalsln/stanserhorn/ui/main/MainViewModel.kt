package com.digitalsln.stanserhorn.ui.main

import com.digitalsln.stanserhorn.base.ChannelListeningViewModel
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.WifiConnectionState
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import com.digitalsln.stanserhorn.tools.WifiStateChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    wifiStateChannel: WifiStateChannel,
    private val dataUpdateManager: DataUpdateManager,
    private val preferenceHelper: PreferenceHelper,
): ChannelListeningViewModel<MainViewState, WifiConnectionState>(wifiStateChannel) {

    override fun initState() = MainViewState()

    override fun onDataUpdateEvent(event: WifiConnectionState) {
        when(event) {
            WifiConnectionState.WifiWasConnected -> { updateState { copy(wifiState = event) } }
            else -> updateState { copy(wifiState = event) }
        }
    }

    fun canNavigateWhenWifiLost() = preferenceHelper.automaticallyOpenAddTripLog
    fun getCorrectWifiName() = preferenceHelper.correctWifiSsid

    fun startDataUpdateManager() {
        dataUpdateManager.start()
    }

    fun stopDataUpdateManager() {
        dataUpdateManager.stop()
    }
}