package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeagueDto(
    @field:Json(name = "idLeague") val id: String?,
    @field:Json(name = "strLeague") val name: String?,
    @field:Json(name = "strSport") val sport: String?
)
