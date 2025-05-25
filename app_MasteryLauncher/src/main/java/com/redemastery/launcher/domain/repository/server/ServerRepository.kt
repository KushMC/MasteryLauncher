package com.redemastery.launcher.domain.repository.server

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.data.remote.dto.UpdateInfoResponse
import com.redemastery.launcher.domain.model.ServerStatus
import com.redemastery.launcher.domain.model.ServerUpdate
import com.redemastery.launcher.domain.model.UpdateInfo
import kotlinx.coroutines.flow.Flow

interface ServerRepository {
    suspend fun getStatus(): Flow<IResult<ServerStatus>>
    suspend fun getUpdateInfo(): Flow<IResult<UpdateInfo>>
    suspend fun getServerUpdate(): Flow<IResult<ServerUpdate>>


}