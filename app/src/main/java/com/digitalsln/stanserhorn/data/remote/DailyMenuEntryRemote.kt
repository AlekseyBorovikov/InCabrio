package com.digitalsln.stanserhorn.data.remote

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName="entry")
data class DailyMenuEntryRemote(
    @JacksonXmlProperty(localName="menu_id") val menuId: Long,
    @JacksonXmlProperty(localName="menu_seq") val menuSeq: Int,
    @JacksonXmlProperty(localName="menu_titel") val menuTitle: String,
    @JacksonXmlProperty(localName="menu_text") val menuText: String,
    @JacksonXmlProperty(localName="menu_datum") val menuDate: String
)