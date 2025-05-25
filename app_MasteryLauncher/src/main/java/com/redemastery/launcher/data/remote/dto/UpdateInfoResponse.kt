package com.redemastery.launcher.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.redemastery.launcher.domain.model.UpdateInfo

data class UpdateInfoResponse(
    @SerializedName("version") var version: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("shop_link") var shopLink: String?,
    @SerializedName("discord_invite") var discordInvite: String?,
){
    fun toUpdateInfo(): UpdateInfo {
        return UpdateInfo(
            version = this.version,
            url = this.url,
            shopLink = this.shopLink,
            discordInvite = this.discordInvite
        )
    }
}