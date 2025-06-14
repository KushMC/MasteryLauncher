package com.redemastery.launcher.presentation.features.launcher.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.redemastery.design.composable.LogoMasteryLauncher
import com.redemastery.design.theme.ApplicationTheme
import com.redemastery.launcher.R
import com.redemastery.launcher.core.parseMinecraftColorCodes
import com.redemastery.launcher.domain.model.Motd
import com.redemastery.launcher.presentation.compose.lifecycle.LifeCycleEffect
import com.redemastery.launcher.presentation.features.launcher.ui.composable.DownloadState
import com.redemastery.launcher.presentation.features.launcher.ui.composable.DownloadStatusButton
import com.redemastery.launcher.presentation.features.launcher.ui.context_aware.ContextAwareDoneListenerObserver
import com.redemastery.launcher.presentation.features.launcher.ui.domain.model.LauncherEvent
import com.redemastery.launcher.presentation.features.launcher.ui.domain.screen.LauncherScreens
import com.redemastery.oldapi.pojav.JMinecraftVersionList
import com.redemastery.oldapi.pojav.extra.ExtraConstants
import com.redemastery.oldapi.pojav.extra.ExtraCore
import com.redemastery.oldapi.pojav.tasks.AsyncVersionList
import com.redemastery.oldapi.pojav.tasks.AsyncVersionList.VersionDoneListener
import java.util.UUID


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LauncherScreen(
    onOpenInternet: (String) -> Unit,
    onLaunchGame: () -> Unit
) {

    val viewModel = hiltViewModel<LauncherViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    var screens: LauncherScreens by remember {
        mutableStateOf(LauncherScreens.LaunchGame)
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val account = state.account
    val serverStatus = state.serverStatus
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.snackbarMessages.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopBar(
                username = account?.username,
                motd = serverStatus?.motd,
                isBeta = state.isBeta,
                showMotd = screens == LauncherScreens.LaunchGame,
                onSettingsClick = {
                    screens = LauncherScreens.Settings
                },
                onUserClick = {
                    screens = LauncherScreens.LaunchGame
                }
            )
        },
        bottomBar = {
            if (screens == LauncherScreens.LaunchGame) BottomBar(
                serverStatus?.isOnline == true,
                serverStatus?.players?.online ?: 0,
                onDiscord = {
                    state.discordLink?.let {
                        onOpenInternet(it)
                    } ?: run {
                        viewModel.showToast("Link não encontrado.")
                    }
                },
                onShop = {
                    state.shopLink?.let {
                        onOpenInternet(it)
                    } ?: run {
                        viewModel.showToast("Link não encontrado.")
                    }
                },
                onRetryDownload = {
                    viewModel.onEvent(LauncherEvent.RetryDownload)
                },
                onLaunchGame = onLaunchGame
            )
        }
    ) { _ ->
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = ApplicationTheme.spacing.gigantic + 8.dp
                    ),
                targetState = screens,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith (slideOutHorizontally { width -> -width } + fadeOut())
                }
            ) { screen ->

                when (screen) {
                    is LauncherScreens.LaunchGame -> {
                        Column(Modifier.fillMaxSize()) {}
                    }

                    is LauncherScreens.Settings -> {
                        ConfigScreen(
                            config = state.options,
                            onSave = {
                                viewModel.onEvent(LauncherEvent.SaveConfig(it))
                            },
                            onRetry = {
                                viewModel.onEvent(LauncherEvent.LoadConfig)
                            }
                        )
                    }

                }

            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(ApplicationTheme.spacing.medium),
                snackbar = { data -> Snackbar(snackbarData = data) }
            )
        }
    }
}

@Composable
private fun BottomBar(
    isOnline: Boolean,
    playerOnline: Int,
    onDiscord: () -> Unit,
    onShop: () -> Unit,
    onRetryDownload: () -> Unit,
    onLaunchGame: () -> Unit
) {

    var downloadState = ContextAwareDoneListenerObserver.downloadState
    val context = LocalContext.current as Activity
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = ApplicationTheme.spacing.medium)
            .background(Color.Transparent)
            .animateContentSize(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.small),
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .clickable {
                        onDiscord()
                    }
                    .padding(ApplicationTheme.spacing.mediumSmall),
                verticalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.extraSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = Color.Transparent,
                ) {
                    Image(
                        painter = painterResource(R.drawable.discord_icon),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(ApplicationTheme.spacing.extraGigantic)
                    )
                }

                Text(
                    text = "Discord",
                    style = ApplicationTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ApplicationTheme.colors.onSurface
                )
            }

            Column(
                modifier = Modifier
                    .clickable {
                        onShop()
                    }
                    .animateContentSize()
                    .padding(ApplicationTheme.spacing.mediumSmall),
                verticalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.extraSmall),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(color = Color.Transparent) {
                    Image(
                        painter = painterResource(R.drawable.shop_icon),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier.size(ApplicationTheme.spacing.extraGigantic)
                    )
                }

                Text(
                    text = "Acesse a Loja",
                    style = ApplicationTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ApplicationTheme.colors.onSurface
                )
            }
        }


        Column(
            modifier = Modifier
                .padding(ApplicationTheme.spacing.small)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.extraSmall),
            horizontalAlignment = Alignment.End
        ) {
            DownloadStatusButton(
                modifier = Modifier
                    .wrapContentSize()
                    .animateContentSize(),
                state = downloadState,
                onLaunchClicked = {
                    if(downloadState == DownloadState.ERROR){
                        onRetryDownload()
                    }else{
                        onLaunchGame()
                    }
                }
            )
            Spacer(Modifier.height(ApplicationTheme.spacing.medium))

            Row(
                modifier = Modifier
                    .animateContentSize()
                    .padding(horizontal = ApplicationTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Podcasts,
                    contentDescription = null,
                    tint = ApplicationTheme.colors.tertiary
                )
                Spacer(
                    modifier = Modifier.width(ApplicationTheme.spacing.small)
                )
                Text(
                    text = if (isOnline) "Online" else "Offline",
                    style = ApplicationTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isOnline) ApplicationTheme.colors.tertiary else ApplicationTheme.colors.error
                )
                if (isOnline) {
                    Spacer(modifier = Modifier.width(ApplicationTheme.spacing.small))
                    Text(
                        text = "|",
                        style = ApplicationTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ApplicationTheme.colors.onSurface
                    )
                    Spacer(modifier = Modifier.width(ApplicationTheme.spacing.small))
                    Text(
                        text = "$playerOnline Jogadores",
                        style = ApplicationTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = ApplicationTheme.colors.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun TopBar(
    username: String?,
    motd: Motd?,
    showMotd: Boolean,
    isBeta: Boolean = false,
    onSettingsClick: () -> Unit,
    onUserClick: () -> Unit = {},
) {

    val uuid = remember { UUID.randomUUID() }
    val imageUrl = "https://crafatar.com/renders/head/$uuid"

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build()
    )

    Box(
        Modifier
            .animateContentSize()
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .animateContentSize()
                .fillMaxSize()
                .blur(
                    radius = 120.dp,
                    edgeTreatment = BlurredEdgeTreatment.Unbounded
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .background(
                        ApplicationTheme.colors.surface.copy(
                            alpha = 0.7f
                        ),
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(
                        horizontal = ApplicationTheme.spacing.medium,
                        vertical = ApplicationTheme.spacing.small
                    )
                    .animateContentSize(),
                verticalArrangement = Arrangement.spacedBy(ApplicationTheme.spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopLogo()
                if (showMotd) {
                    motd?.let {
                        Text(
                            modifier = Modifier.padding(horizontal = ApplicationTheme.spacing.small),
                            text = parseMinecraftColorCodes(motd.raw),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if(isBeta) {
                Box(modifier = Modifier.wrapContentSize()) {
                    val transition = rememberInfiniteTransition()
                    val shimmerTranslate by transition.animateFloat(
                        initialValue = -300f,
                        targetValue = 1000f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    val shimmerBrush = Brush.linearGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        start = Offset(shimmerTranslate, 0f),
                        end = Offset(shimmerTranslate + 200f, 200f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.beta),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .height(ApplicationTheme.spacing.extraLarge)
                                .width(ApplicationTheme.spacing.enormous)
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(shimmerBrush)
                                .zIndex(1f)
                        )
                    }
                }
            }


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
                        text = username ?: "",
                        color = ApplicationTheme.colors.onSurface,
                        style = ApplicationTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(icon = Icons.Default.Settings, onClick = onSettingsClick)
            }
        }
    }

    LifeCycleEffect(
        onCreate = {
            AsyncVersionList().getVersionList(VersionDoneListener { versions: JMinecraftVersionList? ->
                ExtraCore.setValue(
                    ExtraConstants.RELEASE_TABLE,
                    versions
                )
            }, false)

        }
    )
}

@Composable
private fun TopLogo() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LogoMasteryLauncher(modifier = Modifier.size(ApplicationTheme.spacing.extraHuge))
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
}

@Composable
fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = Modifier
            .wrapContentSize()
            .padding(start = ApplicationTheme.spacing.medium),
        onClick = onClick,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White)
    }
}
