package com.digitalsln.stanserhorn.data.locale.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry

/**
 * interface for working with the info_board table
 * */
@Dao
interface InfoBoardDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(infoBoardEntry: InfoBoardEntry)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(infoBoardList: List<InfoBoardEntry>)

    @Update
    fun update(infoBoardEntry: InfoBoardEntry): Int

    @Query("SELECT * FROM info_board")
    suspend fun getAllItem(): List<InfoBoardEntry>

    @Query("SELECT * FROM info_board WHERE show = 1")
    suspend fun getAllShowItem(): List<InfoBoardEntry>

    @Query("SELECT id FROM info_board")
    suspend fun getAllIds(): List<Long>

    @Query("DELETE FROM info_board")
    suspend fun deleteAll()

    @Query("DELETE FROM info_board WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)
}