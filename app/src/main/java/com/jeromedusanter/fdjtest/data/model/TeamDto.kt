package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamDto(
    @field:Json(name = "idTeam") val idTeam: String?,
    @field:Json(name = "strTeam") val strTeam: String?,
    @field:Json(name = "strTeamBadge") val strTeamBadge: String?,
    @field:Json(name = "strLeague") val strLeague: String?
)
