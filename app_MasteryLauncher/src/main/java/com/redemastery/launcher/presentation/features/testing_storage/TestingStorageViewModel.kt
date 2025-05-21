package com.redemastery.launcher.presentation.features.testing_storage

import androidx.lifecycle.viewModelScope
import com.redemastery.launcher.core.IResult.Companion.watchStatus
import com.redemastery.launcher.core.base.BaseViewModel
import com.redemastery.launcher.domain.usecase.storage.NotEnoughStorage
import com.redemastery.launcher.domain.usecase.storage.PermissionDenied
import com.redemastery.launcher.domain.usecase.storage.PrepareLauncherUseCase
import com.redemastery.launcher.domain.usecase.storage.StorageUseCase
import com.redemastery.launcher.presentation.features.testing_storage.domain.model.TestingStorageScreenState
import com.redemastery.launcher.presentation.features.testing_storage.domain.model.TestingStorageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestingStorageViewModel @Inject constructor(
    private val storageUseCase: StorageUseCase
): BaseViewModel<TestingStorageState, Unit>(TestingStorageState()){

    override fun onEvent(event: Unit) {

    }

    fun checkStorage(){
        viewModelScope.launch {
            storageUseCase.prepareLauncher(Unit).collect {
                it.watchStatus(
                    onSuccess = { success ->
                        _state.update {
                            it.copy(
                                screenState = if(success) TestingStorageScreenState.ForgeInstall else {
                                    TestingStorageScreenState.Success
                                },
                                progress = 1f,
                                message = if(success) "Instalando Forge" else "Abrindo Launcher"
                            )
                        }
                    },
                    onLoading = { message, progress ->
                        _state.update {
                            it.copy(
                                message = message,
                                progress = progress,
                                screenState = TestingStorageScreenState.Loading
                            )
                        }
                    },
                    onFailed = {

                        when(it){
                            is PermissionDenied -> {
                                _state.update {
                                    it.copy(
                                        screenState = TestingStorageScreenState.Error
                                    )
                                }
                            }

                            is NotEnoughStorage -> {
                                _state.update {
                                    it.copy(
                                        screenState = TestingStorageScreenState.Error
                                    )
                                }
                            }

                            else -> {

                            }
                        }
                    }
                )
            }
        }
    }

}