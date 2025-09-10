package com.vladabur.wubbalubba.data.network.models

import com.google.gson.annotations.SerializedName
import com.vladabur.wubbalubba.domain.models.Location
import com.vladabur.wubbalubba.domain.models.ModelMapper

data class LocationResponse(
    @SerializedName("name")
    val name: String?,
) {
    companion object : ModelMapper<LocationResponse, Location> {
        override fun map(model: LocationResponse): Location = Location(
            name = model.name
        )

    }
}