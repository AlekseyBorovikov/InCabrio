package com.digitalsln.stanserhorn.ui.trip_log

import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.base.ChannelListeningViewModel
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.repositoies.TripLogRepository
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripLogViewModel @Inject constructor(
    dataUpdateChannel: DataUpdateChannel,
    private val repository: TripLogRepository,
    private val preference: PreferenceHelper,
): ChannelListeningViewModel<TripLogState, DataUpdateEvent>(dataUpdateChannel) {

    init {
        getTripLogList()
        updateState { copy(cabineNumber = preference.cabinNumber) }
    }

    override fun initState() = TripLogState()

    override fun onDataUpdateEvent(event: DataUpdateEvent) {
        if (event is DataUpdateEvent.TripLogUpdated) getTripLogList()
    }

    private fun getTripLogList() = viewModelScope.launch {
        val list = repository.getAllTripLogList()
        val ascentNumber = list.sumOf { if(it.ascent) it.numberPassengers else 0 }
        val descentNumber = list.sumOf { if(!it.ascent) it.numberPassengers else 0 }
        updateState { copy(tripLogList = list, ascentNumber = ascentNumber, descentNumber = descentNumber) }
    }

}