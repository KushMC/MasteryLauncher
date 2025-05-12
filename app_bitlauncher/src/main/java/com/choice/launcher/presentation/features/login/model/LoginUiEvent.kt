package com.choice.launcher.presentation.features.login.model

sealed class LoginUiEvent {
    data class LoginPirate(val nickname: String) : LoginUiEvent()
    object LoginMicrosoft : LoginUiEvent()

}