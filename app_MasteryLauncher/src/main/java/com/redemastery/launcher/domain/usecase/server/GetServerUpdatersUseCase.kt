package com.redemastery.launcher.domain.usecase.server

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.UseCase
import com.redemastery.launcher.domain.model.ServerUpdate
import com.redemastery.launcher.domain.repository.server.ServerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServerUpdatersUseCase @Inject constructor(
    private val repository: ServerRepository
): UseCase<Unit, ServerUpdate>{

    override suspend fun invoke(input: Unit): Flow<IResult<ServerUpdate>> {
        return repository.getServerUpdate()
    }
}