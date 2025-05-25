package com.redemastery.launcher.presentation.features.launcher.ui.domain.model

import com.redemastery.launcher.domain.model.MinecraftConfig

sealed class LauncherEvent {

    data class SaveConfig(val config: MinecraftConfig) : LauncherEvent()
    object LoadConfig : LauncherEvent()
    object RetryDownload : LauncherEvent()
}