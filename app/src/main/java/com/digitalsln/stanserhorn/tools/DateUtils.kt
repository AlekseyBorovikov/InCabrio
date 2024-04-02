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

}