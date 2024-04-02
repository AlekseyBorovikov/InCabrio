package com.digitalsln.stanserhorn.ui.daily_menu

import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.base.ChannelListeningViewModel
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.repositoies.DailyMenuRepository
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyMenuViewModel @Inject constructor(
    dataUpdateChannel: DataUpdateChannel,
    private val repository: DailyMenuRepository,
): ChannelListeningViewModel<DailyMenuState, DataUpdateEvent>(dataUpdateChannel) {

    init { getDailyMenuList() }

    override fun onDataUpdateEvent(event: DataUpdateEvent) {
        if (event is DataUpdateEvent.DailyMenuUpdated) { getDailyMenuList() }
    }

    override fun initState() = DailyMenuState()

    private fun getDailyMenuList() = viewModelScope.launch {
        val result = repository.getAllDailyMenuList()
        updateState { copy(dailyMenuList = result) }
    }
}