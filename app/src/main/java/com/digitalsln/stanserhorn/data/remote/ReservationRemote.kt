package com.digitalsln.stanserhorn.data.remote

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName="reservationen")
data class ReservationRemote(
    @JacksonXmlProperty(localName="title")
    val title: String?,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName="entry")
    val entries: List<ReservationEntryRemote>?
)
