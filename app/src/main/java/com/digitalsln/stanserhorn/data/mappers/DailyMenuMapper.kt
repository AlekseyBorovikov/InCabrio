package com.digitalsln.stanserhorn.data.mappers

import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry
import com.digitalsln.stanserhorn.data.remote.DailyMenuEntryRemote
import com.digitalsln.stanserhorn.tools.DateUtils

object DailyMenuMapper {

    fun mapFromRemote(remoteEntry: DailyMenuEntryRemote) = DailyMenuEntry(
        id = remoteEntry.menuId,
        itemNumber = remoteEntry.menuSeq,
        title = remoteEntry.menuTitle,
        text = remoteEntry.menuText,
        date = remoteEntry.menuDate,
        show = isShow(remoteEntry.menuDate)
    )

    private fun isShow(menuDate: String) = DateUtils.isDateCurrent(menuDate)

}