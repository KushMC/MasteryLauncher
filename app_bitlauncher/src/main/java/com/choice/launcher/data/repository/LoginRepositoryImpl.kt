package com.choice.launcher.data.repository

import com.choice.launcher.core.IResult
import com.choice.launcher.domain.model.UserAccount
import com.choice.launcher.domain.repository.login.LoginRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor() : LoginRepository {
    override suspend fun loginPirate(nickname: String): Flow<IResult<UserAccount>> {
        return flow {
            emit(IResult.Loading())
            delay(1000L)
            runCatching {
                val userAccount = UserAccount().apply {
                    this.username = nickname
                }
                userAccount.save()
                userAccount
            }.onSuccess {
                emit(IResult.Success(it))
            }.onFailure {
                emit(IResult.Failed(it))
            }
        }
    }

    override suspend fun loginMicrosoft(): Result<Unit> {
        TODO("Not yet implemented")
    }
}