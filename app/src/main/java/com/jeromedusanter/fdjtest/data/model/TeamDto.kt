package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamDto(
    @Json(name = "idTeam") val idTeam: String?,
    @Json(name = "strTeam") val strTeam: String?,
    @Json(name = "strTeamBadge") val strTeamBadge: String?,
    @Json(name = "strLeague") val strLeague: String?
)
