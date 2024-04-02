package com.digitalsln.stanserhorn.ui.main

import com.digitalsln.stanserhorn.base.ViewState
import com.digitalsln.stanserhorn.data.WifiConnectionState

data class MainViewState(
    val wifiState: WifiConnectionState = WifiConnectionState.Default,
): ViewState