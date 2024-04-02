package com.digitalsln.stanserhorn.data

sealed class WifiConnectionState {

    data object DataIsLoading: WifiConnectionState()
    data object DataWasLoaded: WifiConnectionState()
    data object NoConnection: WifiConnectionState()
    data object LastSyncOutdated: WifiConnectionState()
    data object ShowConnectionError: WifiConnectionState()
    data object WifiWasConnected: WifiConnectionState()
    data object Default: WifiConnectionState()
    data object WrongConnection: WifiConnectionState()
}