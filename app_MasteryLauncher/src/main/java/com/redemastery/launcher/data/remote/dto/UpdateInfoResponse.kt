package com.redemastery.launcher.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.redemastery.launcher.domain.model.UpdateInfo

data class UpdateInfoResponse(
    @SerializedName("version") var version: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("shop_link") var shopLink: String?,
    @SerializedName("discord_invite") var discordInvite: String?,
    @SerializedName("is_beta") var isBeta: Boolean?,
    @SerializedName("server_ip") var serverIp: String?,
    @SerializedName("server_port") var serverPort: Int?,
){
    fun toUpdateInfo(): UpdateInfo {
        return UpdateInfo(
            version = this.version,
            url = this.url,
            shopLink = this.shopLink,
            discordInvite = this.discordInvite,
            isBeta = this.isBeta,
            serverIp = this.serverIp,
            serverPort = this.serverPort
        )
    }

    fun getVersionAndUrl() : UpdateInfo {
        return UpdateInfo(
            version = this.version,
            url = this.url,
        )
    }
}