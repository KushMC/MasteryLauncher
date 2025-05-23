package com.redemastery.launcher.presentation.features.launcher.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.redemastery.design.theme.ApplicationTheme

sealed class DownloadState {
    data object IDLE : DownloadState()
    data class DOWNLOADING(val progress: Int)  : DownloadState()
    data object ERROR : DownloadState()
    data object COMPLETED : DownloadState()
}

@Composable
fun DownloadStatusButton(
    modifier: Modifier = Modifier,
    state: DownloadState,
    onLaunchClicked: () -> Unit
) {
    val animatedBackground by animateColorAsState(
        targetValue = when (state) {
            is DownloadState.IDLE -> ApplicationTheme.colors.tertiary
            is DownloadState.DOWNLOADING -> ApplicationTheme.colors.primary.copy(alpha = 0.8f)
            is DownloadState.COMPLETED -> ApplicationTheme.colors.secondary
            is DownloadState.ERROR -> Color.Red.copy(alpha = 0.8f)
        },
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(ApplicationTheme.shapes.medium)
            .background(animatedBackground)
            .clickable(
                enabled = state == DownloadState.IDLE,
                onClick = onLaunchClicked
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(ApplicationTheme.spacing.medium)
                .alpha(if (state is DownloadState.DOWNLOADING) 0.7f else 1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Crossfade(targetState = state) { currentState ->
                when (currentState) {
                    is DownloadState.IDLE ->
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Launch",
                            modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                            tint = ApplicationTheme.colors.onSurface
                        )

                    is DownloadState.DOWNLOADING ->
                        CircularProgressIndicator(
                            progress = { currentState.progress.toFloat() / 1f },
                            modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                            color = ApplicationTheme.colors.onSurface,
                            strokeWidth = 2.dp,
                            trackColor = ApplicationTheme.colors.surface,
                        )

                    is DownloadState.COMPLETED ->
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                            tint = ApplicationTheme.colors.onSurface
                        )

                    is DownloadState.ERROR ->
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = "Error",
                            modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                        )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                text = when (state) {
                    is DownloadState.IDLE -> "JOGAR"
                    is DownloadState.DOWNLOADING -> "BAIXANDO... ${(state.progress).toInt()}%"
                    is DownloadState.COMPLETED -> "ABRINDO JOGO"
                    is DownloadState.ERROR -> "TENTE NOVAMENTE"
                },
                style = ApplicationTheme.typography.headlineSmall.copy(
                    color = ApplicationTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            )

            if (state is DownloadState.DOWNLOADING) {
                Canvas(modifier = Modifier.wrapContentSize()) {
                    drawArc(
                        color = Color.Green.copy(alpha = 0.2f),
                        startAngle = -90f,
                        sweepAngle = 360f * state.progress,
                        useCenter = false,
                        style = Stroke(width = 4f)
                    )
                }
            }
        }
    }
}