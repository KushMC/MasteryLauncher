package com.redemastery.launcher.presentation.features.launcher.ui

import android.app.Activity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.redemastery.design.composable.LogoMasteryLauncher
import com.redemastery.design.theme.ApplicationTheme
import com.redemastery.launcher.R
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.presentation.features.launcher.screen.LauncherScreens
import com.redemastery.launcher.presentation.features.launcher.ui.composable.DownloadState
import com.redemastery.launcher.presentation.features.launcher.ui.composable.DownloadStatusButton
import com.redemastery.launcher.presentation.features.launcher.ui.context_aware.ContextAwareDoneListenerObserver
import com.redemastery.oldapi.pojav.LauncherActivity.launchMine
import com.redemastery.oldapi.pojav.PojavProfile
import java.util.UUID


@Composable
fun LauncherScreen() {

    var screens: LauncherScreens by remember {
        mutableStateOf(LauncherScreens.LaunchGame)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopBar(
                username = "test",
                onSettingsClick = {
                    screens = LauncherScreens.Settings
                },
                onUserClick = {
                    screens = LauncherScreens.LaunchGame
                }
            )
        },
        bottomBar = {
            if(screens == LauncherScreens.LaunchGame) BottomBar()
        }
    ) { innerPadding ->

        AnimatedContent(
            modifier = Modifier.padding(innerPadding),
            targetState = screens
        ) { screen ->

            when(screen){
                is LauncherScreens.LaunchGame -> {

                }

                is LauncherScreens.Settings -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = ApplicationTheme.spacing.medium,
                                vertical = ApplicationTheme.spacing.medium
                            ),
                    ){


                    }
                }

            }

        }

    }
}

@Composable
private fun BottomBar() {

    var downloadState = ContextAwareDoneListenerObserver.downloadState
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    val context = LocalContext.current as Activity
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(
                horizontal = ApplicationTheme.spacing.medium,
            )
            .background(Color.Transparent)
    ) {

        DownloadStatusButton(
            modifier = Modifier.align(Alignment.CenterEnd),
            state = downloadState,
            progress = downloadProgress,
            onLaunchClicked = {
                launchMine(context)
            }
        )
    }
}

@Composable
fun TopBar(
    username: String = "4xvn",
    onSettingsClick: () -> Unit = {},
    onUserClick: () -> Unit = {},
) {

    val imageUrl = "https://crafatar.com/renders/head/${UUID.randomUUID()}"


    Box(
        Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(
                    radius = 120.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.background(ApplicationTheme.colors.surface.copy(
                    alpha = 0.7f
                ),
                    shape = RoundedCornerShape(12.dp),
                )
                    .padding(
                        horizontal = ApplicationTheme.spacing.medium,
                        vertical = ApplicationTheme.spacing.small
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LogoMasteryLauncher(
                    modifier = Modifier.size(ApplicationTheme.spacing.extraHuge)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = ApplicationTheme.typography.headlineSmall.copy(
                                color = ApplicationTheme.colors.tertiary,
                                fontWeight = FontWeight.Bold
                            ).toSpanStyle(),
                            block = {
                                append("Rede ")
                            }
                        )
                        withStyle(
                            style = ApplicationTheme.typography.headlineSmall.copy(
                                color = ApplicationTheme.colors.onSurface,
                                fontWeight = FontWeight.Bold
                            ).toSpanStyle(),
                            block = {
                                append("Mastery")
                            }
                        )
                    }
                )
            }

            // BOTÕES
            Row(verticalAlignment = Alignment.CenterVertically) {

                OutlinedButton(
                    onClick = onUserClick,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = ApplicationTheme.colors.tertiary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                ) {

                    SubcomposeAsyncImage(
                        modifier = Modifier.size(ApplicationTheme.spacing.medium),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        loading = {
                            CircularProgressIndicator(modifier = Modifier.size(ApplicationTheme.spacing.medium))
                        },
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = username,
                        color = ApplicationTheme.colors.onSurface,
                        style = ApplicationTheme.typography.bodyLarge
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButtonCard(icon = Icons.Default.Settings, onClick = onSettingsClick)
            }
        }
    }
}

@Composable
fun IconButtonCard(
    icon: ImageVector,
    onClick: () -> Unit,
    content: @Composable (RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (content != null) {
            content()
        } else {
            Icon(icon, contentDescription = null, tint = Color.White)
        }
    }
}
