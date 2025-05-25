package com.redemastery.launcher.domain.model

data class ServerUpdate(
    val shopLink: String?,
    val discordInvite: String?,
    val isBeta: Boolean = false,
    val serverIp: String?,
    val serverPort: Int?
)
