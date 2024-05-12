package com.digitalsln.stanserhorn.data.mappers

import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry
import com.digitalsln.stanserhorn.data.remote.DailyMenuEntryRemote
import com.digitalsln.stanserhorn.tools.DateUtils

object DailyMenuMapper {

    fun mapFromRemote(remoteEntry: DailyMenuEntryRemote): DailyMenuEntry {
        val dateTime = DateUtils.convertTimeFromFrom(remoteEntry.menuDate + " 00:00")
        return DailyMenuEntry(
            id = remoteEntry.menuId,
            itemNumber = remoteEntry.menuSeq,
            title = remoteEntry.menuTitle,
            text = remoteEntry.menuText,
            time = dateTime.time,
        )
    }

}