package com.redemastery.launcher.presentation.features.ui

import android.Manifest.*
import android.content.Intent
import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.redemastery.design.theme.ApplicationTheme
import com.redemastery.launcher.R
import com.redemastery.launcher.core.navigation.Destination
import com.redemastery.launcher.presentation.compose.navigation.AppNavHost
import com.redemastery.launcher.presentation.features.launcher.navigation.launcherComposable
import com.redemastery.launcher.presentation.features.login.navigation.loginComposable
import com.redemastery.launcher.presentation.features.testing_storage.navigation.testingStorageComposable
import com.redemastery.oldapi.pojav.JavaGUILauncherActivity
import com.redemastery.oldapi.pojav.Tools.DIR_DATA
import com.redemastery.oldapi.pojav.modloaders.ForgeUtils
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainNavigation(
    onOpenSettings: () -> Unit,
    onInstallForge: () -> Unit,
    onOpenInternet: (String) -> Unit,
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

    val animatedRadius by animateDpAsState(
        targetValue = if (currentScreen == Destination.Launcher.route) 2.dp else 40.dp,
        animationSpec = tween(durationMillis = 500)
    )


    Box(
        modifier = Modifier.fillMaxSize(),
    ){
        Image(
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radius = animatedRadius,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
                .shadow(
                    elevation = 100.dp,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp,
                        bottomStart = 12.dp,
                        bottomEnd = 12.dp
                    ),
                    clip = true
                ),
            painter = painterResource(id = R.drawable.launcher_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        AppNavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController
        ) {
            testingStorageComposable(
                navController = navController,
                onOpenSettings = onOpenSettings,
                onInstallForge = onInstallForge
            )

            loginComposable(
                modifier = Modifier.fillMaxSize(),
                navController = navController,
            )

            launcherComposable(
                modifier = Modifier.fillMaxSize(),
                onOpenInternet = onOpenInternet
            )
        }
    }
}