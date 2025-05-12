package com.choice.launcher.domain.usecase.login

import javax.inject.Inject

data class LoginUseCase(
    val pirate: LoginPirateUseCase,
)