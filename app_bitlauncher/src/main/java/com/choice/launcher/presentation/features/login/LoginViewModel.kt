package com.choice.launcher.presentation.features.login

import androidx.lifecycle.viewModelScope
import com.choice.launcher.core.IResult.Companion.watchStatus
import com.choice.launcher.core.base.BaseViewModel
import com.choice.launcher.domain.usecase.login.LoginUseCase
import com.choice.launcher.presentation.features.login.model.LoginUiEvent
import com.choice.launcher.presentation.features.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginUiState, LoginUiEvent>(LoginUiState()) {

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
                onLoading = {
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