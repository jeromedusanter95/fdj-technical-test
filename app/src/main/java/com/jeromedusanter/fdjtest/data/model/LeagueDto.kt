package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeagueDto(
    @Json(name = "idLeague") val idLeague: String?,
    @Json(name = "strLeague") val strLeague: String?,
    @Json(name = "strSport") val strSport: String?
)
