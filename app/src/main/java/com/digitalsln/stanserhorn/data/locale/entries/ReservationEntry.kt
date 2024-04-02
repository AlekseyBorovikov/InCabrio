package com.digitalsln.stanserhorn.data.locale.entries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservation")
data class ReservationEntry(
    @PrimaryKey val id: Long,
    val ticketColor: String,
    val type: String,
    val status: String,
    val lastChanged: String,
    val agency: String,
    val tourNumber: String,
    val guideLastName: String,
    val guideFirstName: String,
    val occasion: String,
    val date: String,
    val timeAscent: String,
    val timeDescent: String,
    val numberAdults: Int,
    val numberKids: Int,
    val numberBabies: Int,
    val numberDisabled: Int,
    val show: Boolean,
)