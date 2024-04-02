package com.digitalsln.stanserhorn.di

import android.content.Context
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.repositoies.DailyMenuRepository
import com.digitalsln.stanserhorn.repositoies.InfoBoardRepository
import com.digitalsln.stanserhorn.repositoies.InternalLogRepository
import com.digitalsln.stanserhorn.repositoies.ReservationRepository
import com.digitalsln.stanserhorn.repositoies.TripLogRepository
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.DataUpdateManager
import com.digitalsln.stanserhorn.tools.LocaleWifiManager
import com.digitalsln.stanserhorn.tools.NetworkHelper
import com.digitalsln.stanserhorn.tools.WifiStateChannel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Singleton
    @Provides
    fun provideDataUpdateChannel() = DataUpdateChannel()

    @Singleton
    @Provides
    fun provideWifiStateChannel() = WifiStateChannel()

    @Singleton
    @Provides
    fun provideLocaleWifiManager(
        @ApplicationContext context: Context,
        preferenceHelper: PreferenceHelper,
        wifiStateChannel: WifiStateChannel,
    ) = LocaleWifiManager(context, preferenceHelper, wifiStateChannel)

    @Singleton
    @Provides
    fun provideDataUpdaterService(
        @ApplicationContext context: Context,
        wifiStateChannel: WifiStateChannel,
        wifiManager: LocaleWifiManager,
        preferenceHelper: PreferenceHelper,
        infoBoardRepository: InfoBoardRepository,
        dailyMenuRepository: DailyMenuRepository,
        tripLogRepository: TripLogRepository,
        reservationRepository: ReservationRepository,
        internalLogRepository: InternalLogRepository,
    ) = DataUpdateManager(
        context = context,
        wifiStateChannel = wifiStateChannel,
        wifiManager = wifiManager,
        infoBoardRepository = infoBoardRepository,
        dailyMenuRepository = dailyMenuRepository,
        tripLogRepository = tripLogRepository,
        reservationRepository = reservationRepository,
        preferenceHelper = preferenceHelper,
        internalLogRepository = internalLogRepository,
    )

    @Singleton
    @Provides
    fun provideNetworkHelper(preferenceHelper: PreferenceHelper) = NetworkHelper(preferenceHelper)

}
