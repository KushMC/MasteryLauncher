package com.redemastery.launcher.data.repository.user_account

import android.content.Context
import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.di.manager.MCOptionManager
import com.redemastery.launcher.domain.model.MinecraftConfig
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.domain.repository.account.UserAccountRepository
import com.redemastery.oldapi.pojav.PojavProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserAccountRepositoryImpl @Inject constructor(
    private val context: Context
): UserAccountRepository {
    override fun getUserAccount(): Flow<IResult<UserAccount?>> {
        return flow {
            try {
                val user = PojavProfile.getCurrentProfileContents(context, null)
                emit(IResult.Companion.success(user))
            } catch (e: Exception) {
                emit(IResult.Failed(e))
            }
        }
    }

    override fun saveOption(option: MinecraftConfig): Flow<IResult<MinecraftConfig>> {
        return flow {
            try {
                MCOptionManager.config = option
                MCOptionManager.save()
                emit(IResult.Companion.success(option))
            } catch (e: Exception) {
                emit(IResult.Failed(e))
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getOption(): Flow<IResult<MinecraftConfig>> {
        return flow {
            try {
                MCOptionManager.load()
                emit(IResult.Companion.success(MCOptionManager.config))
            } catch (e: Exception) {
                emit(IResult.Failed(e))
            }
        }.flowOn(Dispatchers.IO)
    }
}