package com.redemastery.launcher.domain.usecase.account

import javax.inject.Inject

data class AccountUseCase @Inject constructor(
    val get: GetUserAccountUseCase
)