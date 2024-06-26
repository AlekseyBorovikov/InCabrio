package com.digitalsln.stanserhorn.data.locale.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_log")
data class TripLogEntry(
    @PrimaryKey(autoGenerate = true) val internalId: Long,
    val globeId: Int?,
    val deviceUID: String,
    val cabinNumber: Int,
    val tripOfDay: Int,
    val time: Long,
    val numberPassengers: Int,
    val ascent: Boolean,
    val remarks: String,
    val updated: Boolean,
)