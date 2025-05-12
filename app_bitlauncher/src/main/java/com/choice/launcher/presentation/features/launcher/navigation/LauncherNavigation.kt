package com.choice.launcher.presentation.features.launcher.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.choice.launcher.core.navigation.Destination
import com.choice.launcher.presentation.compose.navigation.appComposable
import com.choice.launcher.presentation.features.launcher.ui.LauncherScreen
import com.choice.launcher.presentation.features.login.LoginScreen

fun NavGraphBuilder.launcherComposable(
    modifier: Modifier,
    navController: NavHostController
) {
    this.appComposable(destination = Destination.Launcher) {
        LauncherScreen()
    }
}