package com.digitalsln.stanserhorn.ui.main

import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.base.ChannelListeningViewModel
import com.digitalsln.stanserhorn.data.WifiConnectionState
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import com.digitalsln.stanserhorn.tools.LocaleWifiManager
import com.digitalsln.stanserhorn.tools.WifiStateChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    wifiStateChannel: WifiStateChannel,
    private val dataUpdateManager: DataUpdateManager,
    private val localeWifiManager: LocaleWifiManager,
): ChannelListeningViewModel<MainViewState, WifiConnectionState>(wifiStateChannel) {

    init { dataUpdateManager.start() }

    override fun initState() = MainViewState()

    override fun onDataUpdateEvent(event: WifiConnectionState) {
        when(event) {
            WifiConnectionState.WifiWasConnected -> { updateState { copy(wifiState = event) } }
            else -> updateState { copy(wifiState = event) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataUpdateManager.stop()
    }
}