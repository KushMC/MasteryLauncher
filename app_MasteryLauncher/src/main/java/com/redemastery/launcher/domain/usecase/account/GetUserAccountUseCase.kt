package com.redemastery.launcher.domain.usecase.account

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.UseCase
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.domain.repository.account.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserAccountUseCase @Inject constructor(
    private val repository: UserAccountRepository
): UseCase<Unit, UserAccount?>{
    override suspend fun invoke(input: Unit): Flow<IResult<UserAccount?>> {
        return repository.getUserAccount()
    }

}
