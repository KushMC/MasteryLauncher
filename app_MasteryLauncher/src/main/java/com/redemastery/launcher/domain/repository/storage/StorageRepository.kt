package com.redemastery.launcher.domain.repository.storage

import android.net.Uri
import com.redemastery.launcher.core.IResult
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    fun isStorageAllowed(): Boolean
    fun hasEnoughStorage(): Boolean
    fun loadPreferencesAndUnpack()
    suspend fun copyRuntimeAsset(fileName: String): Flow<IResult<String>>
    suspend fun installRuntime(): Flow<IResult<Unit>>
    fun isRuntimeInstalled(): Boolean
    fun isForgeInstalled(versionName: String): Boolean
}