package com.vladabur.wubbalubba.data.network.models

import com.google.gson.annotations.SerializedName
import com.vladabur.wubbalubba.domain.models.CharactersList
import com.vladabur.wubbalubba.domain.models.ModelMapper

data class CharactersListResponse(
    @SerializedName("info")
    val info: InfoResponse?,
    @SerializedName("results")
    val results: List<CharacterResponse>?
) {
    companion object : ModelMapper<CharactersListResponse, CharactersList> {
        override fun map(model: CharactersListResponse): CharactersList = CharactersList(
            total = model.info?.count,
            characters = model.results?.map { CharacterResponse.map(it) },
        )
    }
}