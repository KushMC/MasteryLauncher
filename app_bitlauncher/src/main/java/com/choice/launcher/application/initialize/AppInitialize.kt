package com.choice.launcher.application.initialize

import android.content.Context
import android.content.Intent
import net.kdt.pojavlaunch.Architecture
import net.kdt.pojavlaunch.FatalErrorActivity
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.tasks.AsyncAssetManager

object AppInitializer {

    suspend fun safeInit(context: Context) {
        try {
            if (Tools.checkStorageRoot(context)) {
                LauncherPreferences.loadPreferences(context)
            } else {
                Tools.initEarlyConstants(context)
            }

            Tools.DEVICE_ARCHITECTURE = Architecture.getDeviceArchitecture()
            AsyncAssetManager.unpackRuntime(context.assets)

        } catch (ex: Throwable) {
            context.startActivity(
                Intent(context, FatalErrorActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    putExtra("throwable", ex)
                    putExtra("storageAllowed", false)
                }
            )
        }
    }
}
