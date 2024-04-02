package com.digitalsln.stanserhorn.repositoies

import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.locale.dao.InfoBoardDao
import com.digitalsln.stanserhorn.data.locale.entries.InfoBoardEntry
import com.digitalsln.stanserhorn.data.mappers.InfoBoardMapper
import com.digitalsln.stanserhorn.data.remote.InfoBoardRemote
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.tools.NetworkHelper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import javax.inject.Inject

class InfoBoardRepository @Inject constructor(
    private val dataUpdateChannel: DataUpdateChannel,
    private val infoBoardDao: InfoBoardDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkHelper: NetworkHelper,
) {

    private val dummyInfoBoardIds = listOf(100000L, 100001L, 100002L, 100003L, 100004L, 100005L, 100006L, 100007L, 100008L, 100009L)

    suspend fun fillDummyInfoBoardList() {
        val dummyInfoBoardList = mutableListOf<InfoBoardEntry>()
        dummyInfoBoardIds.forEach {
            dummyInfoBoardList.add(
                InfoBoardEntry(
                    id = it,
                    from = "2013.10.30",
                    until = "2013.10.30",
                    message = "Lorem ipsum dolor sit amet consectetur. At feugiat varius et pellentesque non risus. Faucibus non ac suspendisse nec parturient molestie. Cras mattis ac rhoncus aliquam egestas neque nunc volutpat. Sem in in ornare non. Sem ornare tristique commodo consectetur venenatis quam tempor magna. Purus urna bibendum ac in cras non massa pharetra. Aliquam volutpat scelerisque gravida dignissim rhoncus. Feugiat facilisi lectus risus et dictum. Id neque porta etiam dictum laoreet enim gravida leo.",
                    creator = "Alex Miller",
                    dateCreated = "2012.10.30",
                    show = true,
                )
            )
        }
        infoBoardDao.insert(dummyInfoBoardList)
        dataUpdateChannel.send(DataUpdateEvent.InfoBoardUpdated)
    }

    suspend fun removeDummyInfoBoardList() {
        infoBoardDao.deleteByIds(dummyInfoBoardIds)
        dataUpdateChannel.send(DataUpdateEvent.InfoBoardUpdated)
    }

    suspend fun getAllInfoBoardList(): List<InfoBoardEntry> = infoBoardDao.getAllShowItem()

    suspend fun synchronizeTable(): Boolean {
        val url = preferenceHelper.infoboardUrl

        val infoBoardRemote = networkHelper.fetchDataFromURL(url) {
            val res = XmlMapper().readValue(it, InfoBoardRemote::class.java)
            res
        } ?: return false

        // получаем все id
        val allIds = infoBoardDao.getAllIds()
        val listToRemove = allIds.toMutableList()

        return try {
            // update or insert new entries
            infoBoardRemote.entries?.forEach { infoBoardEntry ->
                listToRemove.remove(infoBoardEntry.infoId)
                val localEntry = InfoBoardMapper.mapFromRemote(infoBoardEntry)
                if (infoBoardEntry.infoId in allIds) infoBoardDao.update(localEntry)
                else                                 infoBoardDao.insert(localEntry)
            }
            // remove other entries
            infoBoardDao.deleteByIds(listToRemove)
            dataUpdateChannel.send(DataUpdateEvent.InfoBoardUpdated)
            true
        } catch (e: Exception) {
            Logger.e("In InfoBoardRepository.synchronizeTable: Could not parse XML ('${e.message}').", e)
            false
        }
    }

}