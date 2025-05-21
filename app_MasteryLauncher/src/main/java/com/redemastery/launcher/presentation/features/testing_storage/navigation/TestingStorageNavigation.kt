package com.redemastery.launcher.presentation.features.testing_storage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.redemastery.launcher.core.navigation.Destination
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.presentation.compose.navigation.appComposable
import com.redemastery.launcher.presentation.compose.navigation.navigate
import com.redemastery.launcher.presentation.compose.navigation.popUpTo
import com.redemastery.launcher.presentation.features.testing_storage.TestingStorageScreen


fun NavGraphBuilder.testingStorageComposable(
    navController: NavHostController,
    onOpenSettings: () -> Unit
) {
    this.appComposable(destination = Destination.TestingStorage) {
        TestingStorageScreen(
            onRequestPermission = onOpenSettings,
            onStartLauncher = {
                navController.navigate(Destination.Login){
                    popUpTo(Destination.TestingStorage){
                        inclusive = true
                    }
                }
            }
        )
    }
}