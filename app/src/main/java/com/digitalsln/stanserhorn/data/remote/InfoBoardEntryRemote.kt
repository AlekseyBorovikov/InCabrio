package com.digitalsln.stanserhorn.data.remote

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName="entry")
data class InfoBoardEntryRemote(
    @JacksonXmlProperty(localName="info_id")
    val infoId: Long,
    @JacksonXmlProperty(localName="info_von")
    val von: String,
    @JacksonXmlProperty(localName="info_bis")
    val bis: String,
    @JacksonXmlProperty(localName="info_message")
    val message: String,
    @JacksonXmlProperty(localName="info_wer")
    val wer: String,
    @JacksonXmlProperty(localName="info_compi")
    val compi: String,
    @JacksonXmlProperty(localName="info_stored")
    val stored: String
)
