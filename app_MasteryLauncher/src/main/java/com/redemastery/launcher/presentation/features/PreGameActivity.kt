package com.redemastery.launcher.presentation.features

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.redemastery.design.theme.MasteryLauncherTheme
import com.redemastery.launcher.presentation.features.missing_storage.MissingStorageActivity
import com.redemastery.launcher.presentation.features.ui.MainNavigation
import com.redemastery.oldapi.pojav.JavaGUILauncherActivity
import com.redemastery.oldapi.pojav.Tools.DIR_DATA
import com.redemastery.oldapi.pojav.lifecycle.ContextExecutor
import com.redemastery.oldapi.pojav.modloaders.ForgeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import androidx.core.net.toUri
import com.redemastery.oldapi.pojav.LauncherActivity.launchMine

@AndroidEntryPoint
class PreGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContent {
            MasteryLauncherTheme {
                MainNavigation(
                    onOpenSettings = {
                        Intent(this, MissingStorageActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    },
                    onInstallForge = {
                        Intent(this, JavaGUILauncherActivity::class.java).apply {
                            ForgeUtils.addAutoInstallArgs(this@apply, File(DIR_DATA, "forge_installers.jar"), true)
                            startActivity(this)
                        }
                    },
                    onOpenInternet = {
                        Intent(Intent.ACTION_VIEW, it.toUri()).apply {
                            startActivity(this@apply)
                        }
                    },
                    onLaunchGame = {
                        launchMine(this)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ContextExecutor.setActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        ContextExecutor.clearActivity()
    }
}