package com.digitalsln.stanserhorn.data.locale.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.digitalsln.stanserhorn.data.locale.entries.InternalSynchronizationLogEntry

/**
 * interface for working with the internal_synchronization_log table
 * */
@Dao
interface InternalSynchronizationLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(internalSynchronizationLogEntry: InternalSynchronizationLogEntry)

    @Query("SELECT * FROM internal_synchronization_log ORDER BY syncDate DESC, syncTime DESC")
    suspend fun getAll(): List<InternalSynchronizationLogEntry>

    @Query("SELECT id FROM internal_synchronization_log ORDER BY syncDate DESC, syncTime DESC")
    suspend fun getAllIds(): List<Int>

    @Query("SELECT * FROM internal_synchronization_log WHERE syncStatus = 1")
    suspend fun getAllSuccess(): List<InternalSynchronizationLogEntry>

    @Query("DELETE FROM internal_synchronization_log")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM internal_synchronization_log")
    fun getCount(): Int

    @Query("DELETE FROM internal_synchronization_log WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>)
}