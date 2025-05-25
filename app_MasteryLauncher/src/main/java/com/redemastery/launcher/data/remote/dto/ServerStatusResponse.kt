package com.redemastery.launcher.data.remote.dto

import com.google.gson.annotations.SerializedName

data class ServerStatusResponse(
    @SerializedName("online") var online: Boolean?,
    @SerializedName("host") var host: String?,
    @SerializedName("port") var port: Int?,
    @SerializedName("ip_address") var ipAddress: String?,
    @SerializedName("eula_blocked") var eulaBlocked: Boolean?,
    @SerializedName("retrieved_at") var retrievedAt: Long?,
    @SerializedName("expires_at") var expiresAt: Long?,
    @SerializedName("srv_record") var srvRecord: SrvRecord?,
    @SerializedName("version") var version: Version?,
    @SerializedName("players") var players: Players?,
    @SerializedName("motd") var motd: Motd?,
    @SerializedName("icon") var icon: String?,
)

data class SrvRecord(
    @SerializedName("host") val host: String,
    @SerializedName("port") val port: Int
)

data class Version(
    @SerializedName("name_raw") val nameRaw: String,
    @SerializedName("name_clean") val nameClean: String,
    @SerializedName("name_html") val nameHtml: String,
    @SerializedName("protocol") val protocol: Int
)

data class Players(
    @SerializedName("online") val online: Int,
    @SerializedName("max") val max: Int,
    @SerializedName("list") val list: List<String>
)

data class Motd(
    @SerializedName("raw") val raw: String,
    @SerializedName("clean") val clean: String,
    @SerializedName("html") val html: String
)
