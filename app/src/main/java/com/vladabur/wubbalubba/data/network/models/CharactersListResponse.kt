package com.vladabur.wubbalubba.data.network.models

import com.google.gson.annotations.SerializedName

data class CharactersListResponse(
    @SerializedName("results")
    val results: List<CharacterResponse>
)
