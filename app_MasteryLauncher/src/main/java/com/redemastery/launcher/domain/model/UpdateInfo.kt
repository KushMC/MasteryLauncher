package com.redemastery.launcher.domain.model

import com.google.gson.annotations.SerializedName
import com.redemastery.launcher.data.remote.dto.UpdateInfoResponse

data class UpdateInfo(
    val version: String?,
    val url: String?,
    val shopLink: String?,
    val discordInvite: String?,
)