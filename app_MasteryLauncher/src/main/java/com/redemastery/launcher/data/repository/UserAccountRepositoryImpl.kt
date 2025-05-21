package com.redemastery.launcher.data.repository

import android.content.Context
import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.IResult.Companion.success
import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.launcher.domain.repository.account.UserAccountRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import com.redemastery.oldapi.pojav.PojavProfile
import com.redemastery.oldapi.pojav.Tools
import com.redemastery.oldapi.pojav.utils.MCOptionUtils
import com.redemastery.oldapi.pojav.value.launcherprofiles.LauncherProfiles
import javax.inject.Inject

class UserAccountRepositoryImpl @Inject constructor(
    private val context: Context
): UserAccountRepository {
    override fun getUserAccount(): Flow<IResult<UserAccount?>> {
        return flow {
            try {
                val user = PojavProfile.getCurrentProfileContents(context, null)
                emit(success(user))
            }catch (e: Exception){
                emit(IResult.Failed(e))
            }
        }
    }
}