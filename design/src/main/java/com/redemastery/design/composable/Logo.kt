package com.redemastery.design.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.redemastery.design.R
import com.redemastery.design.theme.MasteryLauncherTheme

@Preview
@Composable
fun LogoMasteryLauncherPreview() {
    MasteryLauncherTheme {
        LogoMasteryLauncher()
    }
}

@Composable
fun LogoMasteryLauncher(modifier: Modifier = Modifier) {
    AsyncImage(
        model = R.drawable.logo,
        modifier = modifier,
        contentScale = ContentScale.Crop,
        contentDescription = "Logo Launcher",
    )
}