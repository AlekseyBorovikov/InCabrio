package com.digitalsln.stanserhorn.data.remote

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName="entry")
data class ReservationEntryRemote(
    @JacksonXmlProperty(localName="res_id")
    val resId: Long,
    @JacksonXmlProperty(localName="res_marke")
    val ticketColor: String,
    @JacksonXmlProperty(localName="res_typ")
    val type: String,
    @JacksonXmlProperty(localName="res_status")
    val status: String,
    @JacksonXmlProperty(localName="res_date")
    val lastChanged: String,
    @JacksonXmlProperty(localName="res_agency")
    val agency: String,
    @JacksonXmlProperty(localName="res_tourno")
    val tourNumber: String,
    @JacksonXmlProperty(localName="res_guide_name")
    val guideLastName: String,
    @JacksonXmlProperty(localName="res_vorname")
    val guideFirstName: String,
    @JacksonXmlProperty(localName="res_anlass")
    val occasion: String,
    @JacksonXmlProperty(localName="res_when")
    val date: String,
    @JacksonXmlProperty(localName="res_timeup")
    val timeAscent: String,
    @JacksonXmlProperty(localName="res_timedn")
    val timeDescent: String,
    @JacksonXmlProperty(localName="res_adults")
    val numberAdults: Int,
    @JacksonXmlProperty(localName="res_kids")
    val numberKids: Int,
    @JacksonXmlProperty(localName="res_bebes")
    val numberBabies: Int,
    @JacksonXmlProperty(localName="res_disabled")
    val numberDisabled: Int,
)