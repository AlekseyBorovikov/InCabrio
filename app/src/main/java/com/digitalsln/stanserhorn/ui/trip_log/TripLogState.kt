package com.digitalsln.stanserhorn.ui.trip_log

import com.digitalsln.stanserhorn.base.ViewState
import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry

data class TripLogState(
    val tripLogList: List<TripLogEntry> = listOf(),
    val cabineNumber: String = "0",
    val ascentNumber: Int = 0,
    val descentNumber: Int = 0,
): ViewState