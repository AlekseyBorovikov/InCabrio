package com.digitalsln.stanserhorn.ui.reservation

import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.base.ChannelListeningViewModel
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.repositoies.ReservationRepository
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReservationViewModel @Inject constructor(
    dataUpdateChannel: DataUpdateChannel,
    private val repository: ReservationRepository,
): ChannelListeningViewModel<ReservationState, DataUpdateEvent>(dataUpdateChannel) {

    init { getReservationList() }

    override fun initState() = ReservationState()

    override fun onDataUpdateEvent(event: DataUpdateEvent) {
        if (event is DataUpdateEvent.ReservationUpdated) getReservationList()
    }

    private fun getReservationList() = viewModelScope.launch {
        val list = repository.getAllReservationList()
        updateState { copy(reservationList = list) }
    }

}