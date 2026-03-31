package com.jeromedusanter.fdjtest.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LeaguesResponse(
    @field:Json(name = "leagues") val leagues: List<LeagueDto>?
)
