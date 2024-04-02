package com.digitalsln.stanserhorn.data.locale.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "info_board")
data class InfoBoardEntry(
    @PrimaryKey val id: Long,
    val from: String,
    val until: String,
    val message: String,
    val creator: String,
    val dateCreated: String,
    val show: Boolean,
)
