package com.redemastery.launcher.domain.usecase.storage

import javax.inject.Inject

data class StorageUseCase @Inject constructor(
    val prepareLauncher: PrepareLauncherUseCase
)