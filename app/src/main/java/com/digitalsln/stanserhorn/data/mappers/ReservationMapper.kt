package com.digitalsln.stanserhorn.data.mappers

import com.digitalsln.stanserhorn.data.locale.entries.ReservationEntry
import com.digitalsln.stanserhorn.data.remote.ReservationEntryRemote
import com.digitalsln.stanserhorn.tools.DateUtils
import com.digitalsln.stanserhorn.tools.Logger
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReservationMapper {

    fun mapFromRemote(remoteEntry: ReservationEntryRemote, latency: Int) = ReservationEntry(
        id = remoteEntry.resId,
        ticketColor = remoteEntry.ticketColor,
        type = remoteEntry.type,
        status = remoteEntry.status,
        lastChanged = remoteEntry.lastChanged,
        agency = remoteEntry.agency,
        tourNumber = remoteEntry.tourNumber,
        guideLastName = remoteEntry.guideLastName,
        guideFirstName = remoteEntry.guideFirstName,
        occasion = remoteEntry.occasion,
        date = remoteEntry.date,
        timeAscent = remoteEntry.timeAscent,
        timeDescent = remoteEntry.timeDescent,
        numberAdults = remoteEntry.numberAdults,
        numberKids = remoteEntry.numberKids,
        numberBabies = remoteEntry.numberBabies,
        numberDisabled = remoteEntry.numberDisabled,
        show = show(remoteEntry.date, remoteEntry.timeAscent, latency),
    )

    fun show(date: String, timeAscent: String, latency: Int): Boolean {
        val reservationToday: Boolean = DateUtils.isDateCurrent(date)
        if (!reservationToday) {
            return false
        }

        // Check if the timeDescent is still not too far away
        if (latency == 0 || timeAscent == "00:00:00") {
            return true
        }
        val nowDate = Date()
        val timeDownFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
        val timeDown: Date = try {
            timeDownFormat.parse("$date $timeAscent")
        } catch (e: ParseException) {
            Logger.e("In ReservationsMapper.show(): Could not parse date string '$date $timeAscent'.", e)
            return true
        }
        return if ((nowDate.time - timeDown.time) / 3600000.0 > latency) {
            false
        } else true
    }

}