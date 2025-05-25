package com.redemastery.launcher.domain.repository.account

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.domain.model.MinecraftConfig
import com.redemastery.launcher.domain.model.UserAccount
import kotlinx.coroutines.flow.Flow

interface UserAccountRepository {

    fun getUserAccount(): Flow<IResult<UserAccount?>>
    fun saveOption(option: MinecraftConfig): Flow<IResult<MinecraftConfig>>
    fun getOption(): Flow<IResult<MinecraftConfig>>

}