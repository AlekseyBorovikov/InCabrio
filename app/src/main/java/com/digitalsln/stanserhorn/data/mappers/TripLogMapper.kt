package com.digitalsln.stanserhorn.data.mappers

import com.digitalsln.stanserhorn.data.locale.entries.TripLogEntry
import com.digitalsln.stanserhorn.data.remote.TripLogEntryRemote
import com.digitalsln.stanserhorn.tools.DateUtils

object TripLogMapper {

    fun mapFromRemote(remoteEntry: TripLogEntryRemote): TripLogEntry {
        val dateTime = DateUtils.convertTimeFromFrom("${remoteEntry.date} ${remoteEntry.time}")

        return TripLogEntry(
            globeId = remoteEntry.id,
            internalId = 0,
            deviceUID = remoteEntry.deviceUID,
            tripOfDay = remoteEntry.tripOfDay,
            cabinNumber = remoteEntry.cabinNumber,
            time = dateTime.time,
            numberPassengers = remoteEntry.numberPassengers,
            ascent = remoteEntry.ascent != 0,
            remarks = remoteEntry.remarks,
            updated = false,
        )
    }

}