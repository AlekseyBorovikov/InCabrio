package com.digitalsln.stanserhorn.base

import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import com.digitalsln.stanserhorn.tools.StateChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

abstract class ChannelListeningViewModel<T: ViewState, R>(stateChannel: StateChannel<R>): BaseViewModel<T>() {

    init {
        viewModelScope.launch {
            stateChannel.observe { event -> onDataUpdateEvent(event) }
        }
    }

    abstract fun onDataUpdateEvent(event: R)

}