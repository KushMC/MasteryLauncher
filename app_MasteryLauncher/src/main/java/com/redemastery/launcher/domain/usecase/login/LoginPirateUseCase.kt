package com.redemastery.launcher.domain.usecase.login

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.UseCase
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.domain.repository.login.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginPirateUseCase @Inject constructor(
    private val repository: LoginRepository
) : UseCase<String, UserAccount> {
    override suspend fun invoke(input: String): Flow<IResult<UserAccount>> {
        return repository.loginPirate(input)
    }

}
