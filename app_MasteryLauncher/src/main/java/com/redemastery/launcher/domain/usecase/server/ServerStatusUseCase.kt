package com.redemastery.launcher.domain.usecase.server

import javax.inject.Inject

data class ServerStatusUseCase @Inject constructor(
    val get: GetServerStatusUseCase,
    val checkVersion: CheckUpdateInfoVersionUseCase,
    val getUpdater: GetServerUpdatersUseCase
)