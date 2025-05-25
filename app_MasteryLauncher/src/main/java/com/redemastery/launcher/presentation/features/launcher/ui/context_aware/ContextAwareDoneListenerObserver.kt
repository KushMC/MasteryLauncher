package com.redemastery.launcher.presentation.features.launcher.ui.context_aware

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.redemastery.launcher.R
import com.redemastery.launcher.presentation.features.launcher.ui.composable.DownloadState
import com.redemastery.oldapi.pojav.MainActivity
import com.redemastery.oldapi.pojav.lifecycle.ContextExecutor
import com.redemastery.oldapi.pojav.lifecycle.ContextExecutorTask
import com.redemastery.oldapi.pojav.progresskeeper.ProgressKeeper
import com.redemastery.oldapi.pojav.tasks.AsyncMinecraftDownloader
import com.redemastery.oldapi.pojav.utils.NotificationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContextAwareDoneListenerObserver(
    private val versionId: String
) : AsyncMinecraftDownloader.DoneListener, ContextExecutorTask {

    companion object {
        var downloadState by mutableStateOf<DownloadState>(DownloadState.IDLE)
        fun updateDownloadState(downloadState: DownloadState){
            this.downloadState = downloadState
        }
    }

    override fun onStartDownload() {
        downloadState = DownloadState.DOWNLOADING(0)
    }

    override fun onDownloadDone() {
        downloadState = DownloadState.COMPLETED
        ProgressKeeper.waitUntilDone {
            ContextExecutor.execute(this)
        }
    }

    override fun onDownloadFailed(throwable: Throwable) {
        downloadState = DownloadState.ERROR
    }

    override fun executeWithActivity(activity: Activity) {
        try {
            downloadState = DownloadState.COMPLETED
            val intent = createGameStartIntent(activity)
            activity.startActivity(intent)
            downloadState = DownloadState.IDLE
            activity.finish()
        } catch (e: Throwable) {
            downloadState = DownloadState.ERROR
        }
    }

    override fun executeWithApplication(context: Context) {
        val intent = createGameStartIntent(context)

        NotificationUtils.sendBasicNotification(
            context,
            R.string.notif_download_finished,
            R.string.notif_download_finished_desc,
            intent,
            NotificationUtils.PENDINGINTENT_CODE_GAME_START,
            NotificationUtils.NOTIFICATION_ID_GAME_START
        )
    }

    private fun createGameStartIntent(context: Context): Intent {
        downloadState = DownloadState.IDLE
        return Intent(context, MainActivity::class.java).apply {
            putExtra(MainActivity.INTENT_MINECRAFT_VERSION, versionId)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
    }
}
