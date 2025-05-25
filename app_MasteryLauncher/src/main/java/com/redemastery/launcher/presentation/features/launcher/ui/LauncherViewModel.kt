package com.redemastery.launcher.presentation.features.launcher.ui

import androidx.lifecycle.viewModelScope
import com.redemastery.launcher.core.IResult.Companion.watchStatus
import com.redemastery.launcher.core.base.BaseViewModel
import com.redemastery.launcher.domain.model.MinecraftConfig
import com.redemastery.launcher.domain.usecase.account.AccountUseCase
import com.redemastery.launcher.domain.usecase.server.ServerStatusUseCase
import com.redemastery.launcher.presentation.features.launcher.ui.domain.model.LauncherEvent
import com.redemastery.launcher.presentation.features.launcher.ui.domain.model.LauncherState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val accountUseCase: AccountUseCase,
    private val serverStatusUseCase: ServerStatusUseCase
) : BaseViewModel<LauncherState, LauncherEvent>(LauncherState()) {

    private var serverStatusJob: Job? = null

    private val _snackbarMessages = MutableSharedFlow<String>()
    val snackbarMessages = _snackbarMessages.asSharedFlow()

    init {
        onCheckingVersion()
        onLoadUser()
        onLoadConfig()
        startServerStatusPolling()
    }

    override fun onEvent(event: LauncherEvent) {
        when (event) {
            is LauncherEvent.SaveConfig -> {
                onSaveConfig(event.config)
            }

            is LauncherEvent.LoadConfig -> {
                onLoadUser()
            }

            is LauncherEvent.RetryDownload -> {
                onCheckingVersion()
            }
        }
    }

    private fun onCheckingVersion(){
        viewModelScope.launch {
            serverStatusUseCase.checkVersion(Unit).collect {
                it.watchStatus(
                    onSuccess = { data ->
                        _state.update {
                            it.copy(
                                shopLink = data.shopLink,
                                discordLink = data.discordInvite
                            )
                        }
                    },
                    onFailed = {
                        showSnackbar(
                            "Não foi possivel baixar os mods do servidor.\nTente novamente"
                        )
                    }
                )
            }
        }
    }


    private fun onSaveConfig(config: MinecraftConfig) {
        viewModelScope.launch {
            accountUseCase.saveOptions(config).collect {
                it.watchStatus(
                    onSuccess = { config ->
                        _state.update {
                            it.copy(
                                options = config
                            )
                        }
                    },
                    onFailed = {
                        _state.update {
                            it.copy(
                                options = it.options
                            )
                        }
                        showSnackbar(message = "Erro ao salvar configurações")
                    }
                )
            }
        }
    }

    private fun onLoadUser() {
        viewModelScope.launch {
            accountUseCase.get(Unit).collect {
                it.watchStatus(
                    onSuccess = { account ->
                        _state.update {
                            it.copy(
                                account = account
                            )
                        }
                    }
                )
            }
        }
    }


    private fun onLoadConfig() {
        viewModelScope.launch {
            accountUseCase.getOptions(Unit).collect {
                it.watchStatus(
                    onSuccess = { config ->
                        _state.update {
                            it.copy(
                                options = config
                            )
                        }
                    }
                )
            }
        }
    }


    private fun startServerStatusPolling() {
        serverStatusJob?.cancel()

        serverStatusJob = viewModelScope.launch {
            while (isActive) {
                serverStatusUseCase.get(Unit).collect {
                    it.watchStatus(
                        onSuccess = { status ->
                            _state.update {
                                it.copy(
                                    serverStatus = status
                                )
                            }
                        }
                    )
                }
                delay(20.seconds)
            }
        }
    }

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            _snackbarMessages.emit(message)
        }
    }
}