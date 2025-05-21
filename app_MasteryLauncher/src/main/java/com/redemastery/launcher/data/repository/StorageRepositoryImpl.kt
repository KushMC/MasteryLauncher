package com.redemastery.launcher.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.IResult.Companion.failed
import com.redemastery.launcher.core.IResult.Companion.loading
import com.redemastery.launcher.core.IResult.Companion.success
import com.redemastery.launcher.domain.repository.storage.StorageRepository
import com.redemastery.launcher.utils.MultiRTUtils.getRuntimes
import com.redemastery.launcher.utils.MultiRTUtils.unpackRuntime
import com.redemastery.oldapi.pojav.Tools
import com.redemastery.oldapi.pojav.Tools.DIR_DATA
import com.redemastery.oldapi.pojav.prefs.LauncherPreferences
import com.redemastery.oldapi.pojav.tasks.AsyncAssetManager
import com.redemastery.oldapi.pojav.value.launcherprofiles.LauncherProfiles
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import androidx.core.content.edit

class StorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): StorageRepository {
    override fun isStorageAllowed(): Boolean {
        return isStorageAllowed(context)
    }

    override fun hasEnoughStorage(): Boolean {
        return Tools.checkStorageRoot(context)
    }

    override fun loadPreferencesAndUnpack() {
        LauncherPreferences.loadPreferences(context)
        AsyncAssetManager.unpackComponents(context)
        AsyncAssetManager.unpackSingleFiles(context)
        LauncherProfiles.load()

        LauncherProfiles.mainProfileJson.profiles.firstNotNullOf {
            LauncherPreferences.DEFAULT_PREF.edit() {
                putString(
                    LauncherPreferences.PREF_KEY_CURRENT_PROFILE,
                    it.key
                )
            }
        }
    }

    override suspend fun copyRuntimeAsset(fileName: String): Flow<IResult<String>> {
        return flow {
            emit(IResult.Loading(progress = 0f, message = "Verificando asset '$fileName'"))

            val destFile = File(DIR_DATA, fileName)
            if (destFile.exists()) {
                emit(success(destFile.absolutePath))
                return@flow
            }

            val assetManager = context.assets
            val inputStream = assetManager.open(fileName)
            val totalSize = inputStream.available().takeIf { it > 0 } ?: -1

            inputStream.use { input ->
                var bytesCopied = 0L
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

                FileOutputStream(destFile).use { output ->
                    var read: Int
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                        bytesCopied += read

                        if (totalSize > 0) {
                            val progress = (bytesCopied.toFloat() / totalSize).coerceIn(0f, 1f)
                            emit(IResult.Loading(progress = progress, message = "Copiando asset '$fileName': $bytesCopied/$totalSize bytes"))
                        } else {
                            emit(IResult.Loading(progress = -1f, message = "Copiando asset '$fileName': $bytesCopied bytes (tamanho desconhecido)"))
                        }
                    }
                }

                emit(success(destFile.absolutePath))
            }
        }.flowOn(Dispatchers.IO)
    }


    override suspend fun installRuntime() : Flow<IResult<Unit>>{
        return flow {
            try {
                unpackRuntime(am = context.assets).collect {
                    when (it) {
                        is IResult.Success -> emit(success(Unit))
                        is IResult.Loading -> emit(loading(it.message, it.progress))
                        is IResult.Failed -> emit(failed(it.exception))
                        else -> {}
                    }
                }
            } catch (e: IOException) {
                emit(failed(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun copyAssetFileWithProgress(
        context: Context,
        assetFileName: String,
        destinationPath: String,
        onProgress: suspend (Float) -> Unit
    ) {
        val assetManager = context.assets
        val buffer = ByteArray(1024)
        var bytesCopied = 0L

        assetManager.open(assetFileName).use { inputStream ->
            val totalSize = inputStream.available().toLong()
            File(destinationPath).outputStream().use { outputStream ->
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    bytesCopied += bytesRead
                    val progress = bytesCopied.toFloat() / totalSize
                    onProgress(progress)
                }
            }
        }
    }

    override fun isRuntimeInstalled(): Boolean {
        return getRuntimes().isNotEmpty()
    }

    override fun isForgeInstalled(versionName: String): Boolean {
        val forgeDir = File(Tools.DIR_HOME_VERSION, versionName)
        val forgeJson = File(forgeDir, "$versionName.json")
        return forgeDir.exists() && forgeJson.exists()
    }
}

fun isStorageAllowed(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val writePermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        writePermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED
    }
}