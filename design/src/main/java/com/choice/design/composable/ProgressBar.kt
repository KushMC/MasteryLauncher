package com.choice.design.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.choice.design.theme.ApplicationTheme

@Composable
fun AnimatedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(ApplicationTheme.colors.error, ApplicationTheme.colors.tertiary)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000),
        label = ""
    )

    Canvas(modifier = modifier
        .height(10.dp)
        .fillMaxWidth()) {
        val width = size.width
        val height = size.height

        drawRoundRect(
            color = Color.LightGray,
            size = Size(width, height),
            cornerRadius = CornerRadius(15f, 15f)
        )

        drawRoundRect(
            brush = Brush.linearGradient(colors),
            size = Size(width * animatedProgress, height),
            cornerRadius = CornerRadius(15f, 15f)
        )
    }
}