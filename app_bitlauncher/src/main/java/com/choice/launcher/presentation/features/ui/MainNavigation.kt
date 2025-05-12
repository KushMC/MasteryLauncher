package com.choice.launcher.presentation.features.ui

import android.Manifest.*
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.choice.design.theme.ApplicationTheme
import com.choice.launcher.presentation.compose.navigation.AppNavHost
import com.choice.launcher.presentation.features.launcher.navigation.launcherComposable
import com.choice.launcher.presentation.features.login.navigation.loginComposable
import com.choice.launcher.presentation.features.testing_storage.navigation.testingStorageComposable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainNavigation(
    onOpenSettings: () -> Unit,
) {
    val navController = rememberNavController()
    var currentScreen by remember {
        mutableStateOf("")
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermission = rememberPermissionState(
            permission = permission.POST_NOTIFICATIONS
        )

        LaunchedEffect(Unit) {
            if (!notificationPermission.status.isGranted) {
                notificationPermission.launchPermissionRequest()
            }
        }
    }

    navController.addOnDestinationChangedListener { _, destination, _ ->
        currentScreen = destination.route ?: ""
    }

    AppNavHost(
        modifier = Modifier
            .fillMaxSize()
            .background(ApplicationTheme.colors.background),
        navController = navController
    ) {
        testingStorageComposable(
            navController = navController,
            onOpenSettings = onOpenSettings,
        )

        loginComposable(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )

        launcherComposable(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        )
    }
}