package com.choice.launcher.presentation.features.login.model

import com.choice.launcher.domain.model.UserAccount

data class LoginUiState(
    val loadingState: LoadingState? = null,
    var account: UserAccount? = null
){

    sealed class LoadingState {
        object PirateAccount: LoadingState()
        object MicrosoftAccount : LoadingState()
    }

}