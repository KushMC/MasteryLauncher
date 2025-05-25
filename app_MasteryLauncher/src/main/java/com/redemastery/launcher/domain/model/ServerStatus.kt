package com.redemastery.launcher.domain.model

data class ServerStatus(
    val isOnline: Boolean,
    val motd: Motd,
    val players: Players,
    val version: Version
)

data class Motd(
    val raw: String,
    val clean: String,
    val html: String
)

data class Players(
    val online: Int,
    val max: Int
)

data class Version(
    val name: String
)