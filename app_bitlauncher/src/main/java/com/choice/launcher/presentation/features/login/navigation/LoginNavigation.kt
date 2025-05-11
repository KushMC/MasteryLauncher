package com.choice.launcher.presentation.features.login.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.choice.launcher.base.navigation.Destination
import com.choice.launcher.presentation.compose.navigation.appComposable
import com.choice.launcher.presentation.features.login.LoginScreen
import com.choice.launcher.presentation.features.testing_storage.TestingStorageScreen

fun NavGraphBuilder.loginComposable(
    modifier: Modifier,
    navController: NavHostController
) {
    this.appComposable(destination = Destination.Login) {
        LoginScreen(modifier){

        }
    }
}