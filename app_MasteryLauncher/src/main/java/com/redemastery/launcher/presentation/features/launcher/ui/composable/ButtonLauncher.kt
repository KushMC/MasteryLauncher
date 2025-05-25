package com.redemastery.launcher.presentation.features.launcher.ui.composable

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.redemastery.design.theme.ApplicationTheme

sealed class DownloadState {
    data object IDLE : DownloadState()
    data class DOWNLOADING(val progress: Int)  : DownloadState()
    data object UPDATE  : DownloadState()
    data object CHECKING  : DownloadState()
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
            is DownloadState.DOWNLOADING -> ApplicationTheme.colors.primaryContainer.copy(alpha = 0.8f)
            is DownloadState.COMPLETED -> ApplicationTheme.colors.tertiary
            is DownloadState.ERROR -> Color.Red.copy(alpha = 0.8f)
            is DownloadState.UPDATE -> ApplicationTheme.colors.background.copy(alpha = 0.8f)
            is DownloadState.CHECKING -> ApplicationTheme.colors.primaryContainer.copy(alpha = 0.8f)
        },
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = modifier
            .clip(ApplicationTheme.shapes.medium)
            .background(animatedBackground)
            .clickable(
                enabled = state == DownloadState.IDLE || state == DownloadState.ERROR,
                onClick = onLaunchClicked
            )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .animateContentSize()
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
                    is DownloadState.UPDATE ->
                        CircularProgressIndicator(
                            modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                            color = ApplicationTheme.colors.onSurface,
                            strokeWidth = 2.dp,
                            trackColor = ApplicationTheme.colors.surface,
                        )

                    is DownloadState.CHECKING ->
                        CircularProgressIndicator(
                            modifier = Modifier.size(ApplicationTheme.spacing.mediumLarge),
                            color = ApplicationTheme.colors.onSurface,
                            strokeWidth = 2.dp,
                            trackColor = ApplicationTheme.colors.surface,
                        )
                }
            }

            Spacer(Modifier.width(8.dp))

            Text(
                modifier = Modifier.animateContentSize(),
                text = when (state) {
                    is DownloadState.IDLE -> "JOGAR"
                    is DownloadState.DOWNLOADING -> "BAIXANDO... ${(state.progress).toInt()}%"
                    is DownloadState.COMPLETED -> "ABRINDO JOGO"
                    is DownloadState.ERROR -> "TENTE NOVAMENTE"
                    is DownloadState.UPDATE -> "ATUALIZANDO MODS"
                    is DownloadState.CHECKING -> "VERIFICANDO ATUALIZAÇÕES"
                },
                style = ApplicationTheme.typography.headlineSmall.copy(
                    color = ApplicationTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.End
            )
        }
    }
}