package com.redemastery.launcher.presentation.features.testing_storage

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.redemastery.design.composable.LogoMasteryLauncher
import com.redemastery.design.theme.ApplicationTheme
import com.redemastery.launcher.presentation.compose.lifecycle.LifeCycleEffect
import com.redemastery.launcher.presentation.features.testing_storage.domain.model.TestingStorageScreenState
import com.redemastery.oldapi.pojav.JavaGUILauncherActivity
import com.redemastery.oldapi.pojav.Tools.DIR_DATA
import com.redemastery.oldapi.pojav.modloaders.ForgeUtils
import java.io.File

@Composable
fun TestingStorageScreen(
    onRequestPermission: () -> Unit,
    onStartLauncher: () -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Sucesso: a JavaGUILauncherActivity terminou com sucesso
            val exitCode = result.data?.getIntExtra("exitCode", -1)
            Log.d("LauncherResult", "Sucesso com exitCode=$exitCode")
            onStartLauncher()
        } else {
            Log.e("LauncherResult", "Falhou ou foi cancelado")
            // Trate erro ou cancelamento
        }
    }

    val viewModel = hiltViewModel<TestingStorageViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.screenState == TestingStorageScreenState.Error) {
        onRequestPermission()
    }

    if (state.screenState == TestingStorageScreenState.Success) {
        onStartLauncher()
    }

    if (state.screenState == TestingStorageScreenState.ForgeInstall) {
        val intent = Intent(context, JavaGUILauncherActivity::class.java).apply {
            ForgeUtils.addAutoInstallArgs(this, File(DIR_DATA, "forge_installers.jar"), true)
        }
        launcher.launch(intent)
    }

    LifeCycleEffect(
        onResume = {
            viewModel.checkStorage()
        }
    )

    LoadingScreen(
        modifier = Modifier.fillMaxSize(),
        description = state.message,
        progress = state.progress
    )
}

@Composable
private fun LoadingScreen(
    modifier: Modifier,
    description: String?,
    progress: Float? = null
) {
    Box(modifier = modifier.padding(ApplicationTheme.spacing.medium)) {

        PulsatingLogo(
            modifier = Modifier
                .align(Alignment.Center)
                .size(ApplicationTheme.spacing.enormous)
        )

        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.medium)
        ) {
            description?.let {
                Text(
                    style = ApplicationTheme.typography.titleLarge.copy(
                        color = ApplicationTheme.colors.onSurface,
                    ),
                    text = "$it..",
                )
            }

            progress?.let {
                LinearProgressIndicator(
                    progress = { it },
                    modifier = Modifier
                        .height(ApplicationTheme.spacing.extraSmall)
                        .fillMaxWidth(),
                    color = ApplicationTheme.colors.primary,
                    trackColor = ApplicationTheme.colors.surface,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@Composable
fun PulsatingLogo(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scaleAnim"
    )

    LogoMasteryLauncher(
        modifier = modifier
            .scale(scale)
    )
}