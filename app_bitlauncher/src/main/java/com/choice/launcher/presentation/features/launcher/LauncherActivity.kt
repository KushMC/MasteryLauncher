package com.choice.launcher.presentation.features.launcher

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.choice.design.theme.BitLauncherTheme
import com.choice.launcher.presentation.features.launcher.ui.LauncherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BitLauncherTheme {
                LauncherScreen()
            }
        }
    }

}