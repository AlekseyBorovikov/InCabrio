package com.digitalsln.stanserhorn.ui.reservation

import com.digitalsln.stanserhorn.base.ViewState
import com.digitalsln.stanserhorn.data.locale.entries.ReservationEntry

data class ReservationState(
    val reservationList: List<ReservationEntry> = listOf(),
): ViewState