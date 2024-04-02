package com.digitalsln.stanserhorn.data.remote

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName="timetable")
data class TripLogRemote @JsonCreator constructor(
    @JacksonXmlProperty(localName="title")
    val title: String?,
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName="entry")
    var entries: List<TripLogEntryRemote>?
)
