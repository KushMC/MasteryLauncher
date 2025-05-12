package com.choice.launcher.presentation.features

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.choice.design.theme.BitLauncherTheme
import com.choice.launcher.presentation.features.launcher.ui.LauncherScreen
import com.choice.launcher.presentation.features.missing_storage.MissingStorageActivity
import com.choice.launcher.presentation.features.ui.MainNavigation
import dagger.hilt.android.AndroidEntryPoint
import net.kdt.pojavlaunch.LauncherActivity
import net.kdt.pojavlaunch.TestStorageActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitLauncherTheme {
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

}