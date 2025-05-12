package com.choice.launcher.domain.usecase.login

import com.choice.launcher.core.IResult
import com.choice.launcher.core.UseCase
import com.choice.launcher.domain.model.UserAccount
import com.choice.launcher.domain.repository.login.LoginRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginPirateUseCase @Inject constructor(
    private val repository: LoginRepository
) : UseCase<String, UserAccount> {
    override suspend fun invoke(input: String): Flow<IResult<UserAccount>> {
        return repository.loginPirate(input)
    }

}
