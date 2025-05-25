package com.redemastery.launcher.data.repository.server

import androidx.core.content.edit
import com.redemastery.launcher.core.IResult
import com.redemastery.launcher.core.IResult.Companion.failed
import com.redemastery.launcher.core.IResult.Companion.loading
import com.redemastery.launcher.core.IResult.Companion.success
import com.redemastery.launcher.core.performNetworkCall
import com.redemastery.launcher.core.performNetworkCallDownloadAndUnzip
import com.redemastery.launcher.data.remote.api.RedeMasteryApi
import com.redemastery.launcher.data.remote.api.ServerApi
import com.redemastery.launcher.di.network.qualifier.MCStatusAPI
import com.redemastery.launcher.di.network.qualifier.RedeMastery
import com.redemastery.launcher.domain.model.Motd
import com.redemastery.launcher.domain.model.Players
import com.redemastery.launcher.domain.model.ServerStatus
import com.redemastery.launcher.domain.model.UpdateInfo
import com.redemastery.launcher.domain.model.Version
import com.redemastery.launcher.domain.repository.server.ServerRepository
import com.redemastery.launcher.presentation.features.launcher.ui.composable.DownloadState
import com.redemastery.launcher.presentation.features.launcher.ui.context_aware.ContextAwareDoneListenerObserver.Companion.downloadState
import com.redemastery.oldapi.pojav.Tools.DIR_GAME_MODS
import com.redemastery.oldapi.pojav.Tools.DIR_GAME_NEW
import com.redemastery.oldapi.pojav.prefs.LauncherPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class ServerRepositoryImpl @Inject constructor(
    @MCStatusAPI private val api: ServerApi,
    @RedeMastery private val updateApi: RedeMasteryApi,
) : ServerRepository {
    override suspend fun getStatus(): Flow<IResult<ServerStatus>> {
        return flow {

            performNetworkCall {
                api.getServerStatus()
            }.collect { collect ->
                when (val it = collect) {
                    is IResult.Success -> {
                        val serverStatus = ServerStatus(
                            isOnline = it.data.online == true,
                            motd = Motd(
                                raw = it.data.motd?.raw ?: "",
                                clean = it.data.motd?.clean ?: "",
                                html = it.data.motd?.html ?: ""
                            ),
                            players = Players(
                                online = it.data.players?.online ?: 0,
                                max = it.data.players?.max ?: 0
                            ),
                            version = Version(
                                name = it.data.version?.nameClean ?: ""
                            )
                        )

                        emit(success(serverStatus))
                    }

                    else -> {}
                }
            }

        }
    }

    override suspend fun getUpdateInfo(): Flow<IResult<UpdateInfo>> {
        return flow {
            downloadState = DownloadState.CHECKING
            val currentVersion = LauncherPreferences.DEFAULT_PREF.getString("version", "") ?: ""


            performNetworkCall {
                updateApi.getUpdaterInfo()
            }.collect {
                when (it) {
                    is IResult.Success -> {
                        val updateInfo = it.data.toUpdateInfo()

                        if (currentVersion == updateInfo.version) {
                            downloadState = DownloadState.IDLE
                            emit(success(updateInfo))
                            return@collect
                        }

                        if(updateInfo.url == null){
                            emit(failed(NullPointerException("url not found")))
                            return@collect
                        }

                        downloadState = DownloadState.UPDATE
                        val file = File(DIR_GAME_MODS)
                        if (file.exists()) {
                            file.listFiles()?.forEach {
                                it.delete()
                            }
                        }else{
                            file.mkdirs()
                        }


                        performNetworkCallDownloadAndUnzip(
                            fileName = "mods.zip",
                            targetDirectory = File(DIR_GAME_MODS),
                            networkAPICall = { updateApi.download(updateInfo.url) }
                        ).collect {
                            when (it) {
                                is IResult.Success -> {
                                    downloadState = DownloadState.IDLE
                                    LauncherPreferences.DEFAULT_PREF.edit {
                                        putString("version", updateInfo.version)
                                    }
                                    emit(success(updateInfo))
                                }

                                is IResult.Failed -> {
                                    downloadState = DownloadState.ERROR
                                    emit(success(updateInfo))
                                }

                                is IResult.Loading -> {
                                    downloadState = DownloadState.DOWNLOADING(
                                        it.progress?.toInt()?.times(100) ?: 0
                                    )
                                    emit(loading(it.message, it.progress))
                                }

                                else -> {

                                }
                            }
                        }

                    }

                    is IResult.Failed -> {
                        downloadState = DownloadState.ERROR
                        emit(failed(it.exception))
                    }

                    is IResult.Loading -> {
                        downloadState = DownloadState.UPDATE
                        emit(loading(it.message, it.progress))
                    }

                    else -> {}
                }
            }
        }.flowOn(Dispatchers.IO)
    }
}