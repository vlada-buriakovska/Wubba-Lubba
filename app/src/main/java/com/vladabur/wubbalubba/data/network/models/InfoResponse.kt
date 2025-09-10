package com.vladabur.wubbalubba.data.network.models

import com.google.gson.annotations.SerializedName
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.models.ModelMapper

data class InfoResponse(
    @SerializedName("count")
    val count: Int?
)