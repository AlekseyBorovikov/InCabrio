package com.digitalsln.stanserhorn.data.locale.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry

/**
 * interface for working with the trip_log table
 * */
@Dao
interface TripLogDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(tripLogEntry: TripLogEntry)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(tripLogList: List<TripLogEntry>)

    @Update
    fun update(tripLogEntry: TripLogEntry): Int

    @Query("DELETE FROM trip_log")
    suspend fun deleteAll()

    @Query("DELETE FROM trip_log WHERE globeId IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)

    @Query("SELECT * FROM trip_log")
    suspend fun getAllItem(): List<TripLogEntry>

    @Query("SELECT globeId FROM trip_log")
    suspend fun getAllIds(): List<Int>

    @Query("SELECT * FROM trip_log WHERE globeId is null or updated = 1")
    suspend fun getCreatedOrUpdated(): List<TripLogEntry>

    // sorted by time

    @Query("SELECT * FROM trip_log WHERE time > :time AND cabinNumber=:cabinNumber ORDER BY time DESC, tripOfDay DESC")
    suspend fun getAllShowItem(cabinNumber: String, time: Long): List<TripLogEntry>

    @Query("SELECT * FROM trip_log WHERE time > :time AND cabinNumber=:cabinNumber ORDER BY tripOfDay DESC")
    suspend fun getTripLogsByShow(cabinNumber: String, time: Long): List<TripLogEntry>

    @Query("SELECT * FROM trip_log WHERE cabinNumber=:cabinNumber AND time > :time ORDER BY time DESC, tripOfDay DESC")
    suspend fun getTripLogsSortedByDate(cabinNumber: String, time: Long): List<TripLogEntry>

    @Query("SELECT sum(numberPassengers) FROM trip_log WHERE ascent = 1 AND time > :time")
    suspend fun getAscentNumber(time: Long): Int?

    @Query("SELECT sum(numberPassengers) FROM trip_log WHERE ascent = 0 AND time > :time")
    suspend fun getDescentNumber(time: Long): Int?
}