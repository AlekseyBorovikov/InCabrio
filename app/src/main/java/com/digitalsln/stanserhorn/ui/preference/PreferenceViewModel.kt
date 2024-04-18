package com.digitalsln.stanserhorn.ui.preference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.digitalsln.stanserhorn.repositoies.DailyMenuRepository
import com.digitalsln.stanserhorn.repositoies.InfoBoardRepository
import com.digitalsln.stanserhorn.repositoies.ReservationRepository
import com.digitalsln.stanserhorn.repositoies.TripLogRepository
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import com.digitalsln.stanserhorn.tools.LocaleWifiManager
import com.digitalsln.stanserhorn.tools.NetworkHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferenceViewModel @Inject constructor(
    private val dailyMenuRepository: DailyMenuRepository,
    private val reservationRepository: ReservationRepository,
    private val tripLogRepository: TripLogRepository,
    private val infoBoardRepository: InfoBoardRepository,
    private val dataUpdateManager: DataUpdateManager,
    private val networkHelper: NetworkHelper,
    private val wifiManager: LocaleWifiManager,
): ViewModel() {

    fun fillDummyData() = viewModelScope.launch {
        dailyMenuRepository.fillDummyDailyMenuList()
        reservationRepository.fillDummyReservationList()
        tripLogRepository.fillDummyTripLogList()
        infoBoardRepository.fillDummyInfoBoardList()
    }

    fun removeDummyData() = viewModelScope.launch {
        dailyMenuRepository.removeDummyDailyMenuList()
        reservationRepository.removeDummyReservationList()
        tripLogRepository.removeDummyTripLogList()
        infoBoardRepository.removeDummyInfoBoardList()
    }

    fun recreateClient() { networkHelper.recreateClient() }

    fun updateLoadInterval() {
        dataUpdateManager.restart()
    }

    fun checkCurrentsSsid() {
      wifiManager.checkCurrentConnectionAndExecuteOrConnect()
    }

}