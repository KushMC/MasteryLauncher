package com.choice.launcher.presentation.features.testing_storage.navigation

import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.choice.launcher.base.navigation.Destination
import com.choice.launcher.presentation.compose.navigation.appComposable
import com.choice.launcher.presentation.features.missing_storage.MissingStorageActivity
import com.choice.launcher.presentation.features.testing_storage.TestingStorageScreen


fun NavGraphBuilder.testingStorageComposable(
    navController: NavHostController,
    onOpenSettings: () -> Unit
) {
    this.appComposable(destination = Destination.TestingStorage) {
        TestingStorageScreen(
            onRequestPermission = onOpenSettings,
            onStartLauncher = {
                navController.navigate(Destination.Login.route)
            }
        )
    }
}