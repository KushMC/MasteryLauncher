package com.redemastery.launcher.presentation.compose.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.redemastery.launcher.core.navigation.Destination

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Destination = Destination.TestingStorage,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit
) {

    NavHost(
        modifier = modifier.systemBarsPadding(),
        navController = navController,
        startDestination = startDestination.route,
        route = route,
        builder = builder
    )

}

fun NavGraphBuilder.appComposable(
    destination: Destination,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = {
        slideInHorizontally(
            animationSpec = tween(800),
            initialOffsetX = { fullWidth -> fullWidth }
        ) + fadeIn(tween(1000))
    },
    exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = {
        slideOutHorizontally(
            animationSpec = tween(800),
            targetOffsetX = { fullWidth -> -fullWidth }
        ) + fadeOut(tween(1000))
    },
    popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?
    )? = {
        slideInHorizontally(
            animationSpec = tween(800),
            initialOffsetX = { fullWidth -> -fullWidth }
        ) + fadeIn(tween(1000))
    },
    popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?
    )? = {
        slideOutHorizontally(
            animationSpec = tween(800),
            targetOffsetX = { fullWidth -> fullWidth }
        ) + fadeOut(tween(1000))
    },
    content: @Composable AnimatedVisibilityScope.(NavBackStackEntry) -> Unit
) {

    composable(
        route = destination.route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )

}


fun NavHostController.navigate(destination: Destination, builder: NavOptionsBuilder.() -> Unit) = this.navigate(destination.route, builder)
fun NavHostController.navigate(destination: Destination) = this.navigate(destination.route)
fun NavOptionsBuilder.popUpTo(destination: Destination, popUpToBuilder: PopUpToBuilder.() -> Unit = {}) = this.popUpTo(route = destination.route, popUpToBuilder = popUpToBuilder)
