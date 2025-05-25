package com.redemastery.launcher.domain.model

import com.google.gson.annotations.SerializedName
import com.redemastery.launcher.data.remote.dto.UpdateInfoResponse

data class UpdateInfo(
    val version: String? = null,
    val url: String? = null,
    val shopLink: String? = null,
    val discordInvite: String? = null,
    val isBeta: Boolean? = null,
    val serverIp: String? = null,
    val serverPort: Int? = null
)