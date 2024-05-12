package com.digitalsln.stanserhorn.tools

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private val dateFormatParser = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)
    private val dateFormatBuilder = SimpleDateFormat("dd.MM.", Locale.GERMANY)
    private val timeFormatParser = SimpleDateFormat("HH:mm:ss", Locale.GERMANY)
    private val timeFormatBuilder = SimpleDateFormat("HH:mm", Locale.GERMANY)
    private val datetimeFormatParser = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY)

    fun shortenDate(dateString: String): String {
        return try {
            val date = dateFormatParser.parse(dateString)
            dateFormatBuilder.format(date!!)
        } catch (e: ParseException) {
            "ERROR"
        }
    }

    fun truncateSecondsFromTime(timeString: String): String {
        return try {
            val time = timeFormatParser.parse(timeString)
            timeFormatBuilder.format(time!!)
        } catch (e: ParseException) {
            "ERROR"
        }
    }

    fun isDateCurrent(dateString: String): Boolean {
        return try {
            val date = dateFormatParser.parse(dateString)
            isDateCurrent(date)
        } catch (e: ParseException) {
            true
        }
    }

    fun isDateCurrent(date: Date): Boolean {
        val nowCal = Calendar.getInstance()
        nowCal.timeInMillis -= 14400000
        nowCal[Calendar.HOUR_OF_DAY] = 0
        nowCal[Calendar.MINUTE] = 0
        nowCal[Calendar.SECOND] = 0
        nowCal[Calendar.MILLISECOND] = 0
        val nowDate = nowCal.time
        return date == nowDate
    }

    fun convertTimeFromFrom(dateTime: String): Date = kotlin.runCatching {
        datetimeFormatParser.parse(dateTime)
    }.onFailure { e ->
        Logger.e("Could not parse date string '$dateTime'.", e)
    }.getOrThrow() as Date

    fun formatDateToServerDateString(date: Date) = dateFormatParser.format(date)
    fun formatDateToServerTimeString(date: Date) = timeFormatParser.format(date)

    fun getTimeStartForTripLog(): Long {
        val calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) < 3) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 3)
        return calendar.timeInMillis
    }

    fun getStartOfCurrentDayTime(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        return calendar.timeInMillis
    }
}