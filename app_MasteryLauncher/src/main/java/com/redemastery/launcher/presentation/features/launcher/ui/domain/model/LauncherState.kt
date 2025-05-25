package com.redemastery.launcher.presentation.features.launcher.ui.domain.model

import com.redemastery.launcher.domain.model.MinecraftConfig
import com.redemastery.launcher.domain.model.ServerStatus
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.presentation.features.launcher.ui.domain.screen.LauncherScreens

data class LauncherState(
    val screen: LauncherScreens = LauncherScreens.LaunchGame,
    val options: MinecraftConfig? = null,
    val account: UserAccount? = null,
    val serverStatus: ServerStatus? = null,
    val discordLink: String? = null,
    val shopLink: String? = null,
    val isBeta: Boolean = false,
)