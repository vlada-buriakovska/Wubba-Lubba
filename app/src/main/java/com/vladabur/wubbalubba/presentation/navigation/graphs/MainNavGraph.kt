package com.vladabur.wubbalubba.presentation.navigation.graphs

import kotlinx.serialization.Serializable

@Serializable
sealed class MainNavGraph {
    @Serializable
    data object CharactersLibrary : MainNavGraph()
}