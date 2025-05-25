package com.redemastery.launcher.presentation.features.launcher.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.redemastery.launcher.core.navigation.Destination
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.presentation.compose.navigation.appComposable
import com.redemastery.launcher.presentation.features.launcher.ui.LauncherScreen

fun NavGraphBuilder.launcherComposable(
    modifier: Modifier = Modifier,
    onOpenInternet: (String) -> Unit,
) {
    this.appComposable(destination = Destination.Launcher) {
        LauncherScreen(
            onOpenInternet = onOpenInternet
        )
    }
}