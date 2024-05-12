package com.digitalsln.stanserhorn.data.locale.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_menu")
data class DailyMenuEntry(
    @PrimaryKey val id: Long,
    val itemNumber: Int,
    val title: String,
    val text: String,
    val time: Long,
)