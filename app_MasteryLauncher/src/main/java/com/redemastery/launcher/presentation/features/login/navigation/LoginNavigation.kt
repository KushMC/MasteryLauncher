package com.redemastery.launcher.presentation.features.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.redemastery.launcher.core.navigation.Destination
import com.redemastery.launcher.presentation.compose.navigation.appComposable
import com.redemastery.launcher.presentation.compose.navigation.navigate
import com.redemastery.launcher.presentation.compose.navigation.popUpTo
import com.redemastery.launcher.presentation.features.login.LoginScreen

fun NavGraphBuilder.loginComposable(
    modifier: Modifier,
    navController: NavHostController,
) {
    this.appComposable(destination = Destination.Login) {
        LoginScreen(
            modifier = modifier,
            onLoginPirate = {
                navController.navigate(Destination.Launcher){
                    popUpTo(Destination.Login){
                        inclusive = true
                    }
                }
            },
            onMicrosoftLogin = {}
        )
    }
}