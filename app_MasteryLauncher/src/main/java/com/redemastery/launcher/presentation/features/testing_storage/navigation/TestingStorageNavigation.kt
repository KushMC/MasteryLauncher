package com.redemastery.launcher.presentation.features.testing_storage.navigation

import android.app.Activity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.redemastery.launcher.core.navigation.Destination
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.presentation.compose.navigation.appComposable
import com.redemastery.launcher.presentation.compose.navigation.navigate
import com.redemastery.launcher.presentation.compose.navigation.popUpTo
import com.redemastery.launcher.presentation.features.testing_storage.TestingStorageScreen
import com.redemastery.oldapi.pojav.JavaGUILauncherActivity
import com.redemastery.oldapi.pojav.Tools.DIR_DATA
import com.redemastery.oldapi.pojav.modloaders.ForgeUtils
import java.io.File


fun NavGraphBuilder.testingStorageComposable(
    navController: NavHostController,
    onOpenSettings: () -> Unit,
    onInstallForge: () -> Unit
) {


    this.appComposable(destination = Destination.TestingStorage) {
        val context = LocalContext.current
        TestingStorageScreen(
            onRequestPermission = onOpenSettings,
            onStartLauncher = {
                navController.navigate(Destination.Login){
                    popUpTo(Destination.TestingStorage){
                        inclusive = true
                    }
                }
            },
            onInstallForge = onInstallForge
        )
    }
}