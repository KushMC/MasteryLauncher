package com.redemastery.design.theme

import BitPluginsDarkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun MasteryLauncherTheme(
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

