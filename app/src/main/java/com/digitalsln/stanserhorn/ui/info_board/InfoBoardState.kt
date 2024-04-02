package com.digitalsln.stanserhorn.ui.info_board

import com.digitalsln.stanserhorn.base.ViewState
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry

data class InfoBoardState(
    val infoBoardList: List<InfoBoardEntry> = listOf(),
): ViewState