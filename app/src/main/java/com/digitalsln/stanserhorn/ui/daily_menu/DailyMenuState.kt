package com.digitalsln.stanserhorn.ui.daily_menu

import com.digitalsln.stanserhorn.base.ViewState
import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry

data class DailyMenuState(
    val dailyMenuList: List<DailyMenuEntry> = listOf(),
): ViewState