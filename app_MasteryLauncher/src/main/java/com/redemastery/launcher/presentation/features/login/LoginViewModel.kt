package com.redemastery.launcher.presentation.features.login

import androidx.lifecycle.viewModelScope
import com.redemastery.launcher.core.IResult.Companion.watchStatus
import com.redemastery.launcher.core.base.BaseViewModel
import com.redemastery.launcher.domain.usecase.account.AccountUseCase
import com.redemastery.launcher.domain.usecase.login.LoginUseCase
import com.redemastery.launcher.presentation.features.login.model.LoginUiEvent
import com.redemastery.launcher.presentation.features.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val accountUseCase: AccountUseCase
) : BaseViewModel<LoginUiState, LoginUiEvent>(LoginUiState()) {

    init {
        viewModelScope.launch {
            accountUseCase.get(Unit).collect {
                it.watchStatus(
                    onSuccess = { result ->
                        _state.update {
                            it.copy(
                                account = result,
                                loadingState = null
                            )
                        }
                    },
                    onLoading = { _, _ ->
                        _state.update {
                            it.copy(
                                loadingState = LoginUiState.LoadingState.PirateAccount
                            )
                        }
                    },
                    onFailed = {
                        _state.update {
                            it.copy(
                                loadingState = null
                            )
                        }
                    }
                )
            }
        }
    }

    override fun onEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginUiEvent.LoginPirate -> {
                    loginPirate(event.nickname)
                }

                is LoginUiEvent.LoginMicrosoft -> {
                    //TODO
                }
            }
        }
    }

    suspend fun loginPirate(nickname: String) {
        loginUseCase.pirate(nickname).collect {
            it.watchStatus(
                onSuccess = { result ->
                    _state.update {
                        it.copy(
                            loadingState = null,
                            account = result
                        )
                    }
                },
                onLoading = { _, _ ->
                    _state.update {
                        it.copy(
                            loadingState = LoginUiState.LoadingState.PirateAccount
                        )
                    }
                },
                onFailed = {

                }
            )
        }
    }
}