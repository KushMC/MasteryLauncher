package com.redemastery.launcher.domain.usecase.server

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.UseCase
import com.redemastery.launcher.domain.model.UpdateInfo
import com.redemastery.launcher.domain.repository.server.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CheckUpdateInfoVersionUseCase @Inject constructor(
    private val repository: ServerRepository
): UseCase<Unit, UpdateInfo>{

    override suspend fun invoke(input: Unit): Flow<IResult<UpdateInfo>> {
        return repository.getUpdateInfo()
    }

}
