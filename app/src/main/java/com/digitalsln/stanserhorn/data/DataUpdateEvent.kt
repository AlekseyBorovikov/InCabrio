package com.digitalsln.stanserhorn.data

sealed class DataUpdateEvent {
    data object InfoBoardUpdated : DataUpdateEvent()
    data object DailyMenuUpdated : DataUpdateEvent()
    data object ReservationUpdated: DataUpdateEvent()
    data object TripLogUpdated: DataUpdateEvent()
    data object Default: DataUpdateEvent()
}