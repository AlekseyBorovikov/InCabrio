package com.digitalsln.stanserhorn.repositoies

import com.digitalsln.stanserhorn.data.DataUpdateEvent
import com.digitalsln.stanserhorn.data.PreferenceHelper
import com.digitalsln.stanserhorn.data.locale.dao.ReservationDao
import com.digitalsln.stanserhorn.data.locale.entries.ReservationEntry
import com.digitalsln.stanserhorn.data.mappers.ReservationMapper
import com.digitalsln.stanserhorn.data.remote.ReservationRemote
import com.digitalsln.stanserhorn.tools.DataUpdateChannel
import com.digitalsln.stanserhorn.tools.Logger
import com.digitalsln.stanserhorn.tools.NetworkHelper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import javax.inject.Inject

class ReservationRepository @Inject constructor(
    private val dataUpdateChannel: DataUpdateChannel,
    private val reservationDao: ReservationDao,
    private val preferenceHelper: PreferenceHelper,
    private val networkHelper: NetworkHelper,
) {

    private val dummyReservationIds = listOf(100000L, 100001L, 100002L, 100003L, 100004L, 100005L, 100006L, 100007L, 100008L, 100009L)

    suspend fun fillDummyReservationList() {
        val dummyReservationList = mutableListOf<ReservationEntry>()
        dummyReservationIds.forEach {
            dummyReservationList.add(
                ReservationEntry(
                    id = it,
                    ticketColor = "Gray",
                    type = "",
                    status = "",
                    lastChanged = "",
                    agency = "",
                    tourNumber = "",
                    guideLastName = "",
                    guideFirstName = "",
                    occasion = "Lorem ipsum dolor sit amet consectetur. At feugiat varius et pellentesque non risus. Faucibus non ac suspendisse nec parturient molestie.",
                    date = "",
                    timeAscent = "10:25",
                    timeDescent = "11:00",
                    numberAdults = 25,
                    numberKids = 25,
                    numberBabies = 25,
                    numberDisabled = 0,
                    show = true,
                )
            )
        }
        reservationDao.insert(dummyReservationList)
        dataUpdateChannel.send(DataUpdateEvent.ReservationUpdated)
    }

    suspend fun removeDummyReservationList() {
        reservationDao.deleteByIds(dummyReservationIds)
        dataUpdateChannel.send(DataUpdateEvent.ReservationUpdated)
    }

    suspend fun getAllReservationList(): List<ReservationEntry> = reservationDao.getAllShowItem()

    suspend fun synchronizeTable(): Boolean {
        val url = preferenceHelper.reservationsUrl

        val reservationRemote = networkHelper.fetchDataFromURL(url) {
            XmlMapper().readValue(it, ReservationRemote::class.java)
        } ?: return false

        // get all id from table
        val allIds = reservationDao.getAllIds()
        val listToRemove = allIds.toMutableList()

        return try {
            // update or insert new entries
            reservationRemote.entries?.forEach { reservationEntry ->
                listToRemove.remove(reservationEntry.resId)
                val localEntry = ReservationMapper.mapFromRemote(reservationEntry, preferenceHelper.reservationLatency.toIntOrNull() ?: 0)
                if (reservationEntry.resId in allIds) reservationDao.update(localEntry)
                else                                  reservationDao.insert(localEntry)
            }
            // remove other entries
            reservationDao.deleteByIds(listToRemove)
            dataUpdateChannel.send(DataUpdateEvent.ReservationUpdated)
            true
        } catch (e: Exception) {
            Logger.e("In ReservationRepository: Could not parse XML ('${e.message}').", e)
            false
        }
    }

}