package com.digitalsln.stanserhorn.di

import android.content.Context
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.locale.ApplicationDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

const val DB_NAME = "InCabrioDb"
const val PREFS_NAME = "InCabrioPrefs"

@Module
@InstallIn(SingletonComponent::class)
class LocaleModule {

    @Provides
    @Singleton
    fun provideSharedPreference(
        @ApplicationContext context: Context,
    ) = PreferenceHelper(context, PREFS_NAME)

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context) = ApplicationDatabase.getInstance(context, DB_NAME)

    @Provides
    fun provideDailyMenuDao(database: ApplicationDatabase) = database.dailyMenuDao()

    @Provides
    fun provideInfoBoardDao(database: ApplicationDatabase) = database.infoBoardDao()

    @Provides
    fun provideReservationDao(database: ApplicationDatabase) = database.reservationDao()

    @Provides
    fun provideTripLogDao(database: ApplicationDatabase) = database.tripLogDao()

    @Provides
    fun provideInternalSynchronizationLogDao(database: ApplicationDatabase) = database.internalSynchronizationLogDao()

}
