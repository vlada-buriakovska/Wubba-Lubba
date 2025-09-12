package com.vladabur.wubbalubba.data.network.services

import com.vladabur.wubbalubba.data.network.models.CharacterResponse
import com.vladabur.wubbalubba.data.network.models.CharactersListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CharacterService {

    @GET("character")
    suspend fun getCharacters(
        @Query("page")
        page: Int,
        @Query("name")
        name: String?,
        @Query("status")
        status: String? = null,
        @Query("species")
        species: String? = null,
        @Query("gender")
        gender: String? = null,
    ): CharactersListResponse

    @GET("character/{id}")
    suspend fun getCharacter(
        @Path("id")
        id: Int,
    ): CharacterResponse
}