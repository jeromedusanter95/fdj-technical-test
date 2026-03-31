package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamDto(
    @field:Json(name = "idTeam") val id: String?,
    @field:Json(name = "strTeam") val name: String?,
    @field:Json(name = "strBadge") val badgeUrl: String?,
    @field:Json(name = "strLeague") val leagueName: String?
)
