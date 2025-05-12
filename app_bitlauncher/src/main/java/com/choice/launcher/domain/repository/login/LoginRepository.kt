package com.choice.launcher.domain.repository.login

import com.choice.launcher.core.IResult
import com.choice.launcher.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun loginPirate(nickname: String): Flow<IResult<UserAccount>>
    suspend fun loginMicrosoft(): Result<Unit>
}