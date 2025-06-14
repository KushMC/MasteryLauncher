package com.redemastery.launcher.domain.usecase.storage

import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.IResult.Companion.failed
import com.redemastery.launcher.core.IResult.Companion.loading
import com.redemastery.launcher.core.IResult.Companion.success
import com.redemastery.launcher.core.IResult.Companion.watchStatus
import com.redemastery.launcher.core.UseCase
import com.redemastery.launcher.domain.repository.storage.StorageRepository
import com.redemastery.oldapi.pojav.Tools
import com.redemastery.oldapi.pojav.Tools.DIR_DATA
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class PermissionDenied : Exception()
class NotEnoughStorage : Exception()

class PrepareLauncherUseCase @Inject constructor(
    private val repository: StorageRepository
) : UseCase<Unit, Boolean> {

    override suspend fun invoke(input: Unit): Flow<IResult<Boolean>> = flow {
        emit(loading("Preparando Launcher"))

        if (!repository.isStorageAllowed()) {
            emit(failed(PermissionDenied()))
            return@flow
        }

        emit(loading("Verificando espaço em disco"))
        if (!repository.hasEnoughStorage()) {
            emit(failed(NotEnoughStorage()))
            return@flow
        }

        emit(loading("Carregando preferências"))
        repository.loadPreferencesAndUnpack()
        emit(loading("Descompactando assets"))
        val serverDat = File(Tools.DIR_GAME_NEW, "servers.dat")
        repository.copyRuntimeAsset(serverDat).collect {
            if(it is IResult.Loading){
                emit(loading(it.message, it.progress))
            }
        }

        emit(loading("Verificando JRE"))
        val isRuntimeReady = repository.isRuntimeInstalled()

        if (!isRuntimeReady) {
            repository.installRuntime().collect { result ->
                when (result) {
                    is IResult.Success -> { checkForgeInstalled() }
                    is IResult.Loading -> emit(loading(result.message, result.progress))
                    is IResult.Failed -> { emit(failed(result.exception)) }
                    else -> {}
                }
            }
        }else{
            checkForgeInstalled()
        }

    }

    private suspend fun FlowCollector<IResult<Boolean>>.checkForgeInstalled() {
        emit(loading("Verificando Forge 1.7.10"))
        val isForgeInstalled = repository.isForgeInstalled("1.7.10-Forge10.13.4.1614-1.7.10")

        if (!isForgeInstalled) {
            emit(loading("Copiando Forge"))
            repository.copyRuntimeAsset(File(DIR_DATA, "forge_installers.jar")).collect { result ->
                when (result) {
                    is IResult.Success -> emit(success(true))
                    is IResult.Loading -> emit(loading(result.message, result.progress))
                    is IResult.Failed -> emit(failed(result.exception))
                    else -> {}
                }
            }
        } else {
            emit(success(false))
        }
    }
}
                  