package com.choice.design.theme

import BitPluginsDarkColorScheme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun BitLauncherTheme(
    content: @Composable() () -> Unit
) {

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = Color.Transparent,
    )
    systemUiController.setNavigationBarColor(
        color = Color.Transparent,
    )

    MaterialTheme(
        colorScheme = BitPluginsDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}

