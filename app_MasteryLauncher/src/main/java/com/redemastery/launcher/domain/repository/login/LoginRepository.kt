package com.redemastery.launcher.domain.repository.login

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow
import com.redemastery.oldapi.pojav.value.MinecraftAccount

interface LoginRepository {
    suspend fun loginPirate(nickname: String): Flow<IResult<UserAccount>>
    suspend fun loginMicrosoft(): Result<Unit>
}