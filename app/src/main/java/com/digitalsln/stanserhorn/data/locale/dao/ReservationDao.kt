package com.digitalsln.stanserhorn.data.locale.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.digitalsln.stanserhorn.data.locale.entries.ReservationEntry

/**
 * interface for working with the reservation table
 * */
@Dao
interface ReservationDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reservationEntry: ReservationEntry)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reservationList: List<ReservationEntry>)

    @Update
    fun update(reservationEntry: ReservationEntry): Int

    @Query("SELECT * FROM reservation")
    suspend fun getAllItem(): List<ReservationEntry>

    @Query("SELECT * FROM reservation WHERE show = 1 order by date, timeAscent asc")
    suspend fun getAllShowItem(): List<ReservationEntry>

    @Query("SELECT id FROM reservation")
    suspend fun getAllIds(): List<Long>

    @Query("DELETE FROM reservation")
    suspend fun deleteAll()

    @Query("DELETE FROM reservation WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}