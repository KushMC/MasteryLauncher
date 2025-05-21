package com.redemastery.launcher.presentation.features.login.model

import com.redemastery.launcher.domain.model.UserAccount
import com.redemastery.oldapi.pojav.value.MinecraftAccount

data class LoginUiState(
    val loadingState: LoadingState? = null,
    var account: UserAccount? = null
){

    sealed class LoadingState {
        object PirateAccount: LoadingState()
        object MicrosoftAccount : LoadingState()
    }

}