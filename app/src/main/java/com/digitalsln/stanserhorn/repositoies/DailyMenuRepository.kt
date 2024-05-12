package com.digitalsln.stanserhorn.repositoies

import android.util.Log
import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.locale.dao.DailyMenuDao
import com.digitalsln.stanserhorn.data.locale.entries.DailyMenuEntry
import com.digitalsln.stanserhorn.data.mappers.DailyMenuMapper
import com.digitalsln.stanserhorn.data.remote.DailyMenuRemote
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.DateUtils
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.tools.NetworkHelper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import java.util.Date
import javax.inject.Inject


class DailyMenuRepository @Inject constructor(
    private val dataUpdateChannel: DataUpdateChannel,
    private val dailyMenuDao: DailyMenuDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkHelper: NetworkHelper,
) {

    private val dummyDailyMenuIds = listOf(100000L, 100001L, 100002L, 100003L, 100004L, 100005L, 100006L, 100007L, 100008L, 100009L)

    suspend fun fillDummyDailyMenuList() {
        val dummyDailyMenuList = mutableListOf<DailyMenuEntry>()
        dummyDailyMenuIds.forEach {
            dummyDailyMenuList.add(
                DailyMenuEntry(
                    id = it,
                    itemNumber = 0,
                    title = "Pizza",
                    text = "Lorem ipsum dolor sit amet consectetur. At feugiat varius et pellentesque non risus. Faucibus non ac suspendisse nec parturient molestie.",
                    time = Date().time,
                )
            )
        }
        dailyMenuDao.insert(dummyDailyMenuList)
        dataUpdateChannel.send(DataUpdateEvent.DailyMenuUpdated)
    }

    suspend fun removeDummyDailyMenuList() {
        dailyMenuDao.deleteByIds(dummyDailyMenuIds)
        dataUpdateChannel.send(DataUpdateEvent.DailyMenuUpdated)
    }

    suspend fun getAllDailyMenuList(): List<DailyMenuEntry> {
        Log.d("TEST", "get menu")
        val currentTime = DateUtils.getStartOfCurrentDayTime()
        return dailyMenuDao.getAllShowItem(currentTime)
    }

    suspend fun synchronizeTable(): Boolean {
        val url = preferenceHelper.dailyMenuUrl

        val menuRemote = networkHelper.fetchDataFromURL(url) {
            XmlMapper().readValue(it, DailyMenuRemote::class.java)
        } ?: return false

        // get all id
        val allIds = dailyMenuDao.getAllIds()
        val listToRemove = allIds.toMutableList()

        return try {
            // update or insert new entries
            menuRemote.entries?.forEach { menuEntry ->
                listToRemove.remove(menuEntry.menuId)
                val localEntry = DailyMenuMapper.mapFromRemote(menuEntry)
                if (menuEntry.menuId in allIds) dailyMenuDao.update(localEntry)
                else                            dailyMenuDao.insert(localEntry)
            }
            // remove other entries
            dailyMenuDao.deleteByIds(listToRemove)
            dataUpdateChannel.send(DataUpdateEvent.DailyMenuUpdated)
            true
        } catch (e: Exception) {
            Logger.e("In DailyMenuRepository.synchronizeTable: Could not parse XML ('${e.message}').", e)
            false
        }
    }

}