package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamsResponse(
    @field:Json(name = "teams") val teams: List<TeamDto>?
)
