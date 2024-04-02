package com.digitalsln.stanserhorn.data.mappers

import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry
import com.digitalsln.stanserhorn.data.remote.InfoBoardEntryRemote
import com.digitalsln.stanserhorn.tools.Logger
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InfoBoardMapper {

    fun mapFromRemote(remoteEntry: InfoBoardEntryRemote): InfoBoardEntry {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY)
        val newDateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.GERMANY)
        return InfoBoardEntry(
            id = remoteEntry.infoId,
            from = remoteEntry.von,
            until = remoteEntry.bis,
            message = remoteEntry.message,
            creator = remoteEntry.wer,
            dateCreated = newDateFormat.format(dateFormat.parse(remoteEntry.stored)),
            show = show(remoteEntry.von, remoteEntry.bis),
        )
    }

    fun show(from: String, until: String): Boolean {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.GERMANY)
        val fromDate: Date
        val untilDate: Date

        try {
            fromDate = dateFormat.parse(from)
            untilDate = dateFormat.parse(until)
        } catch (e: ParseException) {
            Logger.e("In InfoBoardMapper.show(): Could not parse date string '$from' or '$until'.", e)
            return true
        }

        fromDate.setTime(fromDate.time + 14400000) // Turn forward the clock 4h
        untilDate.setTime(untilDate.time + 100800000) // Turn forward the clock 28h

        val nowDate = Date()
        return nowDate in fromDate..untilDate
    }
}