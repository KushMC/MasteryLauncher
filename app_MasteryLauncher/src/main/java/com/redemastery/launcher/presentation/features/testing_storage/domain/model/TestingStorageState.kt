package com.redemastery.launcher.presentation.features.testing_storage.domain.model

data class TestingStorageState(
    val message: String? = null,
    val progress: Float? = null,
    val screenState: TestingStorageScreenState = TestingStorageScreenState.Loading
)

sealed class TestingStorageScreenState {
    object Loading: TestingStorageScreenState()
    object Error: TestingStorageScreenState()
    object Success: TestingStorageScreenState()
    object ForgeInstall: TestingStorageScreenState()
}