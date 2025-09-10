package com.vladabur.wubbalubba.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vladabur.wubbalubba.presentation.feature.library.CharactersLibraryRoute
import com.vladabur.wubbalubba.presentation.navigation.graphs.MainNavGraph

@Composable
fun MainNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        startDestination = MainNavGraph.CharactersLibrary
    ) {
        composable<MainNavGraph.CharactersLibrary>(
            enterTransition = { fadeIn() + scaleIn() },
            exitTransition = { fadeOut() + scaleOut() }
        ) {
            CharactersLibraryRoute {
                //TODO
            }
        }
    }
}