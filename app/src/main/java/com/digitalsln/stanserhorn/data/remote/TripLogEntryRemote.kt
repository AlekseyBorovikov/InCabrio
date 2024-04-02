package com.digitalsln.stanserhorn.data.remote

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName="entry")
data class TripLogEntryRemote(
    @JacksonXmlProperty(localName="fahrt_globale_id")
    val id: Int,
    @JacksonXmlProperty(localName="fahrt_interne_id")
    val internalId: Long,
    @JacksonXmlProperty(localName="fahrt_geraete_id")
    val deviceUID: String,
    @JacksonXmlProperty(localName="fahrt_kabine")
    val cabinNumber: Int,
    @JacksonXmlProperty(localName="fahrt_fahrtenzaehler_tag")
    val tripOfDay: Int,
    @JacksonXmlProperty(localName="fahrt_datum")
    val date: String,
    @JacksonXmlProperty(localName="fahrt_zeit")
    val time: String,
    @JacksonXmlProperty(localName="fahrt_datum_zeit")
    val dateTime: String,
    @JacksonXmlProperty(localName="fahrt_pax")
    val numberPassengers: Int,
    @JacksonXmlProperty(localName="fahrt_bergfahrt")
    val ascent: Int,
    @JacksonXmlProperty(localName="fahrt_bemerkung")
    val remarks: String,
)