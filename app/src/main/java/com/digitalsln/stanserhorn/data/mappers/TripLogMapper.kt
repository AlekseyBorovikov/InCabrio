package com.digitalsln.stanserhorn.data.mappers

import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry
import com.digitalsln.stanserhorn.data.remote.TripLogEntryRemote
import com.digitalsln.stanserhorn.tools.DateUtils
import com.digitalsln.stanserhorn.tools.Logger
import java.text.DateFormat
import java.text.ParseException
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

object TripLogMapper {

    fun mapFromRemote(remoteEntry: TripLogEntryRemote)= TripLogEntry(
        globeId = remoteEntry.id,
        internalId = 0,
        deviceUID = remoteEntry.deviceUID,
        tripOfDay = remoteEntry.tripOfDay,
        cabinNumber = remoteEntry.cabinNumber,
        date = remoteEntry.date,
        time = remoteEntry.time,
        numberPassengers = remoteEntry.numberPassengers,
        ascent = remoteEntry.ascent != 0,
        remarks = remoteEntry.remarks,
        updated = false,
        show = show(remoteEntry.date, remoteEntry.time),
    )

    fun show(tripDate: String, tripTime: String): Boolean {
        val dateFormat: DateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY)
        val tripDateTime: Date = try {
            dateFormat.parse("$tripDate $tripTime")
        } catch (e: ParseException) {
            Logger.e("In TripLogMapper.show(): Could not parse date string '$tripDate $tripTime'.", e)
            return true
        }
        tripDateTime.setTime(tripDateTime.time - 14400000)
        val tripCal: Calendar = GregorianCalendar()
        tripCal.setTime(tripDateTime)
        tripCal[Calendar.HOUR_OF_DAY] = 0
        tripCal[Calendar.MINUTE] = 0
        tripCal[Calendar.SECOND] = 0
        tripCal[Calendar.MILLISECOND] = 0
        return DateUtils.isDateCurrent(tripCal.time)
    }

}