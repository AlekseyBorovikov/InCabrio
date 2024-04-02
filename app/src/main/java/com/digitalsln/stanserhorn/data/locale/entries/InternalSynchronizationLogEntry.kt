package com.digitalsln.stanserhorn.data.locale.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "internal_synchronization_log")
data class InternalSynchronizationLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val syncDate: String,
    val syncTime: String,
    val syncStatus: Boolean,
)
