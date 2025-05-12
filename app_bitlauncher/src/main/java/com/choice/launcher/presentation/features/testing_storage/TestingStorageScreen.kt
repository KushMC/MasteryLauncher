package com.choice.launcher.presentation.features.testing_storage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.choice.design.composable.LogoBitLauncher
import com.choice.design.theme.ApplicationTheme
import kotlinx.coroutines.delay
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.tasks.AsyncAssetManager

@Composable
fun TestingStorageScreen(
    onRequestPermission: () -> Unit,
    onStartLauncher: () -> Unit
) {
    val context = LocalContext.current
    var loadingStep by remember { mutableIntStateOf(0) }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(loadingStep) {
        when (loadingStep) {
            0 -> {
                description = "Verificando permissões..."
                delay(1000)
                loadingStep = 1
            }

            1 -> {
                description = "Verificando espaço em disco..."
                var hasStorage = isStorageAllowed(context) && checkStorageSpace(context)
                delay(1000)
                if (hasStorage) {
                    description = "Carregando assets..."
                    //Initialize constants (implicitly) and preferences after we confirm that we have storage.
                    LauncherPreferences.loadPreferences(context)
                    AsyncAssetManager.unpackComponents(context)
                    AsyncAssetManager.unpackSingleFiles(context)
                    onStartLauncher()
                } else {
                    description = "Permissões insuficientes"
                    onRequestPermission()
                }
            }
        }
    }

    LoadingScreen(
        modifier = Modifier.fillMaxSize(),
        description = description
    )
}

@Composable
private fun LoadingScreen(
    modifier: Modifier,
    description: String
) {
    Box(modifier = modifier) {

        PulsatingLogo(
            modifier = Modifier
                .align(Alignment.Center)
                .size(ApplicationTheme.spacing.enormous)
        )

        Text(
            modifier = Modifier.align(Alignment.BottomCenter),
            style = ApplicationTheme.typography.titleLarge.copy(
                color = ApplicationTheme.colors.onSurface,
            ),
            text = description,
        )
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

    LogoBitLauncher(
        modifier = modifier
            .scale(scale)
    )
}

fun isStorageAllowed(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val writePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        writePermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED
    }
}

fun checkStorageSpace(context: Context): Boolean {
    return Tools.checkStorageRoot(context)
}
