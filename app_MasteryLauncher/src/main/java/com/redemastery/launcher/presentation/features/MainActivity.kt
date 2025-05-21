package com.redemastery.launcher.presentation.features

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.redemastery.design.theme.MasteryLauncherTheme
import com.redemastery.launcher.presentation.features.missing_storage.MissingStorageActivity
import com.redemastery.launcher.presentation.features.ui.MainNavigation
import com.redemastery.oldapi.pojav.lifecycle.ContextExecutor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MasteryLauncherTheme {
                MainNavigation(
                    onOpenSettings = {
                        Intent(this, MissingStorageActivity::class.java).apply {
                            startActivity(this)
                            finish()
                        }
                    },
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