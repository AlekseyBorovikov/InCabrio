package com.digitalsln.stanserhorn.ui.info_board

import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.base.ChannelListeningViewModel
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.repositoies.DailyMenuRepository
import com.digitalsln.stanserhorn.repositoies.InfoBoardRepository
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoBoardViewModel @Inject constructor(
    dataUpdateChannel: DataUpdateChannel,
    private val repository: InfoBoardRepository,
): ChannelListeningViewModel<InfoBoardState, DataUpdateEvent>(dataUpdateChannel) {

    init { getInfoBoardList() }

    override fun initState() = InfoBoardState()

    override fun onDataUpdateEvent(event: DataUpdateEvent) {
        if (event is DataUpdateEvent.InfoBoardUpdated) getInfoBoardList()
    }

    private fun getInfoBoardList() = viewModelScope.launch {
        val list = repository.getAllInfoBoardList()
        updateState { copy(infoBoardList = list) }
    }

}