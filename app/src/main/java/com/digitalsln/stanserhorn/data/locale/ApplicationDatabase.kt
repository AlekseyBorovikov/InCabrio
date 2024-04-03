package com.digitalsln.stanserhorn.data.locale

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.digitalsln.stanserhorn.data.locale.dao.DailyMenuDao
import com.digitalsln.stanserhorn.data.locale.dao.InfoBoardDao
import com.digitalsln.stanserhorn.data.locale.dao.InternalSynchronizationLogDao
import com.digitalsln.stanserhorn.data.locale.dao.ReservationDao
import com.digitalsln.stanserhorn.data.locale.dao.TripLogDao
import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry
import com.digitalsln.stanserhorn.data.locale.entries.InternalSynchronizationLogEntry
import com.digitalsln.stanserhorn.data.locale.entries.ReservationEntry
import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry

@Database(entities = [InfoBoardEntry::class, DailyMenuEntry::class, InternalSynchronizationLogEntry::class, ReservationEntry::class, TripLogEntry::class], version = 3, exportSchema = false)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun dailyMenuDao(): DailyMenuDao
    abstract fun infoBoardDao(): InfoBoardDao
    abstract fun internalSynchronizationLogDao(): InternalSynchronizationLogDao
    abstract fun reservationDao(): ReservationDao
    abstract fun tripLogDao(): TripLogDao

    companion object {
        @Volatile
        private var instance: ApplicationDatabase? = null

        fun getInstance(context: Context, dbName: String): ApplicationDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, dbName).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context, dbName: String): ApplicationDatabase {
            return Room.databaseBuilder(context, ApplicationDatabase::class.java, dbName)
                .allowMainThreadQueries()
                .enableMultiInstanceInvalidation() // maybe room database create multi instance. add this line solved my problem
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}