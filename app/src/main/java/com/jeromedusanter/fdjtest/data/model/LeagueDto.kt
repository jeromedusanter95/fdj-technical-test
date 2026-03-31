package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeagueDto(
    @field:Json(name = "idLeague") val idLeague: String?,
    @field:Json(name = "strLeague") val strLeague: String?,
    @field:Json(name = "strSport") val strSport: String?
)
