package com.redemastery.launcher.data.repository.login

import android.content.Context
import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.domain.repository.login.LoginRepository
import com.redemastery.oldapi.pojav.PojavProfile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : LoginRepository {
    override suspend fun loginPirate(nickname: String): Flow<IResult<UserAccount>> {
        return flow {
            emit(IResult.Loading())
            delay(1000L)
            runCatching {
                val userAccount = UserAccount(username = nickname)

                userAccount.save()
                PojavProfile.setCurrentProfile(context, userAccount.username)
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