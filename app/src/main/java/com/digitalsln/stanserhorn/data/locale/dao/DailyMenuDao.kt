package com.digitalsln.stanserhorn.data.locale.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry

/**
 * interface for working with the daily_menu table
 * */
@Dao
interface DailyMenuDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(dailyMenuEntry: DailyMenuEntry)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(dailyMenuList: List<DailyMenuEntry>)

    @Update
    fun update(dailyMenuEntry: DailyMenuEntry): Int

    @Query("SELECT * FROM daily_menu")
    suspend fun getAllItem(): List<DailyMenuEntry>

    @Query("SELECT * FROM daily_menu WHERE time >= :time ORDER BY itemNumber ASC")
    suspend fun getAllShowItem(time: Long): List<DailyMenuEntry>

    @Query("DELETE FROM daily_menu")
    suspend fun deleteAll()

    @Query("SELECT id FROM daily_menu")
    suspend fun getAllIds(): List<Long>

    @Query("DELETE FROM daily_menu WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}