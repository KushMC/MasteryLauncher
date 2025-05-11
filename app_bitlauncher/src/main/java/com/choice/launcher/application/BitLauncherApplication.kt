package com.choice.launcher.application

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.choice.launcher.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import net.kdt.pojavlaunch.*
import net.kdt.pojavlaunch.lifecycle.ContextExecutor
import net.kdt.pojavlaunch.prefs.LauncherPreferences
import net.kdt.pojavlaunch.tasks.AsyncAssetManager
import net.kdt.pojavlaunch.utils.FileUtils
import net.kdt.pojavlaunch.utils.LocaleUtils
import java.io.File
import java.io.PrintStream
import java.text.DateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@HiltAndroidApp
class BitLauncherApplication : Application() {

    companion object {
        const val CRASH_REPORT_TAG = "BitLauncherCrashReport"
        var sExecutorService: ExecutorService = Executors.newFixedThreadPool(4)
    }

    private val crashHandler: CrashHandler by lazy { CrashHandler(this) }
    private val initializer: AppInitializer by lazy { AppInitializer(this) }

    override fun onCreate() {
        super.onCreate()
        ContextExecutor.setApplication(this)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> crashHandler.handleException(throwable) }

        initializer.initialize()
    }



    override fun onTerminate() {
        super.onTerminate()
        ContextExecutor.clearApplication()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleUtils.setLocale(base))
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        LocaleUtils.setLocale(this)
    }
}

class AppInitializer(private val application: Application) {

    fun initialize() {
        try {
            if(Tools.checkStorageRoot(application)){
                LauncherPreferences.loadPreferences(application)
            }else{
                Tools.initEarlyConstants(application)
            }
            Tools.DEVICE_ARCHITECTURE = Architecture.getDeviceArchitecture()

            adjustNativeLibraryDir()

            AsyncAssetManager.unpackRuntime(application.assets)
        } catch (exception: Throwable) {
            handleInitializationError(exception)
        }
    }

    private fun adjustNativeLibraryDir() {
        if (Architecture.isx86Device() && Architecture.is32BitsDevice()) {
            val originalJNIDirectory = application.applicationInfo.nativeLibraryDir
            application.applicationInfo.nativeLibraryDir = originalJNIDirectory.substring(0,
                originalJNIDirectory.lastIndexOf("/")) + "/x86"
        }
    }

    private fun handleInitializationError(throwable: Throwable) {
        val ferrorIntent = Intent(application, FatalErrorActivity::class.java).apply {
            putExtra("throwable", throwable)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        application.startActivity(ferrorIntent)
    }
}

class CrashHandler(private val application: Application) {

    fun handleException(th: Throwable) {
        val storagePermAllowed = checkStoragePermission()

        val crashFile = File(if (storagePermAllowed) Tools.DIR_GAME_HOME else Tools.DIR_DATA, "latestcrash.txt")
        saveCrashReport(crashFile, th)

        FatalErrorActivity.showError(application, crashFile.absolutePath, storagePermAllowed, th)
        Tools.fullyExit()
    }

    private fun checkStoragePermission(): Boolean {
        return (Build.VERSION.SDK_INT >= 29 ||
                ActivityCompat.checkSelfPermission(application, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                Tools.checkStorageRoot(application)
    }

    private fun saveCrashReport(crashFile: File, th: Throwable) {
        try {
            FileUtils.ensureParentDirectory(crashFile)
            PrintStream(crashFile).use { crashStream ->
                crashStream.append("BitLauncher crash report\n")
                    .append(" - Time: ").append(DateFormat.getDateTimeInstance().format(Date())).append("\n")
                    .append(" - Device: ").append(Build.PRODUCT).append(" ").append(Build.MODEL).append("\n")
                    .append(" - Android version: ").append(Build.VERSION.RELEASE).append("\n")
                    .append(" - Crash stack trace:\n")
                    .append(" - Launcher version: ${BuildConfig.VERSION_NAME}\n")
                    .append(Log.getStackTraceString(th))
            }
        } catch (throwable: Throwable) {
            Log.e(BitLauncherApplication.CRASH_REPORT_TAG, " - Exception while saving crash stack trace:", throwable)
            Log.e(BitLauncherApplication.CRASH_REPORT_TAG, " - The crash stack trace was:", th)
        }
    }
}