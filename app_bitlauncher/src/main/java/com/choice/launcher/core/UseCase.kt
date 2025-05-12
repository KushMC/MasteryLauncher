package com.choice.launcher.core

import kotlinx.coroutines.flow.Flow

interface UseCase <in I, out O> {
    suspend operator fun invoke(input: I): Flow<IResult<O>>
}