package com.redemastery.launcher.domain.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.annotation.Keep
import com.google.gson.JsonSyntaxException
import com.redemastery.oldapi.pojav.Tools
import okio.IOException
import java.io.File
import kotlin.getValue

@Suppress("IOStreamConstructor")
@Keep
class UserAccount(
    var accessToken: String = "0",
    var clientToken: String = "0",
    var profileId: String = "00000000-0000-0000-0000-000000000000",
    var username: String = "Steve",
    var selectedVersion: String = "1.12.2-forge-14.23.5.2860",
    var isMicrosoft: Boolean = false,
    var msaRefreshToken: String = "0",
    var xuid: String? = null,
    var expiresAt: Long = 0L,
    var skinFaceBase64: String? = null
) {
    @delegate:Transient
    val faceCache: Bitmap? by lazy {
        username.skinFile().takeIf(File::exists)
            ?.let { BitmapFactory.decodeFile(it.absolutePath) }
            ?: skinFaceBase64
                ?.let { Base64.decode(it, Base64.DEFAULT) }
                ?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
    }

    fun updateSkinFace(uuid: String = profileId) = runCatching {
        val file = username.skinFile()
        Tools.downloadFile("https://mc-heads.net/head/$uuid/100", file.path)
    }

    @Throws(IOException::class)
    fun save(outPath: String = username.accountFile().path): String {
        Tools.write(outPath, Tools.GLOBAL_GSON.toJson(this))
        return username
    }

    companion object {
        @Throws(JsonSyntaxException::class)
        fun parse(content: String) =
            Tools.GLOBAL_GSON.fromJson(content, UserAccount::class.java)

        fun load(name: String): UserAccount? =
            runCatching {
                name.accountFile().takeIf(File::exists)
                    ?.let { parse(Tools.read(it.path)) }
                    ?.apply { fillDefaults() }
            }.getOrNull()

        fun getSkinFace(name: String): Bitmap? =
            name.skinFile().takeIf(File::exists)
                ?.let { BitmapFactory.decodeFile(it.path) }

        private fun UserAccount.fillDefaults() {
            if (accessToken.isBlank()) accessToken = "0"
            if (clientToken.isBlank()) clientToken = "0"
            if (profileId.isBlank()) profileId = "00000000-0000-0000-0000-000000000000"
            if (username.isBlank()) username = "Steve"
            if (selectedVersion.isBlank()) selectedVersion = "1.12.2"
            if (msaRefreshToken.isBlank()) msaRefreshToken = "0"
        }

        private fun String.accountFile() = File(Tools.DIR_ACCOUNT_NEW, "$this.json")
        private fun String.skinFile()    = File(Tools.DIR_CACHE, "$this.png")
    }
}
