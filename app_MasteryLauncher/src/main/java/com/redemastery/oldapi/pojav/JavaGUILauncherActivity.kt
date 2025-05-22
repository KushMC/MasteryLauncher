package com.redemastery.oldapi.pojav

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.kdt.LoggerView
import com.redemastery.design.theme.MasteryLauncherTheme
import com.redemastery.launcher.R
import com.redemastery.launcher.presentation.features.testing_storage.LoadingScreen
import com.redemastery.launcher.utils.MultiRTUtils.getNearestJreName
import com.redemastery.oldapi.pojav.multirt.MultiRTUtils
import com.redemastery.oldapi.pojav.multirt.Runtime
import com.redemastery.oldapi.pojav.prefs.LauncherPreferences
import com.redemastery.oldapi.pojav.utils.JREUtils
import kotlinx.coroutines.launch
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.util.Collections
import java.util.zip.ZipFile
import kotlin.math.max
import kotlin.system.exitProcess

class JavaGUILauncherActivity : BaseActivity() {
    private var mTextureView: AWTCanvasView? = null
    private var mLoggerView: LoggerView? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_java_gui_launcher)

        try {
            val latestLogFile = File(Tools.DIR_GAME_HOME, "latestlog.txt")
            if (!latestLogFile.exists() && !latestLogFile.createNewFile()) throw IOException("Failed to create a new log file")
            Logger.begin(latestLogFile.getAbsolutePath())
        } catch (e: IOException) {
            e.printStackTrace()
            Tools.showError(this, e, true)
        }

        mLoggerView = findViewById<LoggerView>(R.id.launcherLoggerView)
        mTextureView = findViewById<AWTCanvasView>(R.id.installmod_surfaceview)

        findViewById<ComposeView>(R.id.composeView).setContent {
            MasteryLauncherTheme {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(
                                radius = 40.dp,
                                edgeTreatment = BlurredEdgeTreatment.Unbounded
                            )
                            .shadow(
                                elevation = 100.dp,
                                shape = RoundedCornerShape(
                                    topStart = 12.dp,
                                    topEnd = 12.dp,
                                    bottomStart = 12.dp,
                                    bottomEnd = 12.dp
                                ),
                                clip = true
                            ),
                        painter = painterResource(id = R.drawable.launcher_background),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )


                    LoadingScreen(
                        modifier = Modifier.fillMaxSize(),
                        description = "Instalando o Forge.. (Pode demorar, não saia da tela).",
                        progress = -1f
                    )
                }
            }
        }

        try {

            val extras = intent.extras
            if (extras == null) {
                finish()
                return
            }

            val javaArgs = extras.getString("javaArgs")
            val resourceUri = extras.getParcelable<Uri>("modUri")


            when {
                javaArgs != null -> {
                    startModInstaller(null, javaArgs)
                }

                resourceUri != null -> {
                    lifecycleScope.launch {
                        startModInstallerWithUri(resourceUri)
                    }
                }
            }
        } catch (th: Throwable) {
            th.printStackTrace()
            Tools.showError(this, th, true)
        }


        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            public override fun handleOnBackPressed() {
                Tools.dialogForceClose(this@JavaGUILauncherActivity)
            }
        })
    }


    private fun startModInstallerWithUri(uri: Uri) {
        try {
            val cacheFile = File(cacheDir, "mod-installer-temp")
            val contentStream = contentResolver.openInputStream(uri)
            if (contentStream == null) throw IOException("Failed to open content stream")
            FileOutputStream(cacheFile).use { fileOutputStream ->
                IOUtils.copy(contentStream, fileOutputStream)
            }
            contentStream.close()
            startModInstaller(cacheFile, null)
        } catch (e: IOException) {
            e.printStackTrace()
            Tools.showError(this, e, true)
        }
    }

    fun selectRuntime(modFile: File?): Runtime? {
        val javaVersion = getJavaVersion(modFile)
        if (javaVersion == -1) {
            finalErrorDialog(getString(R.string.execute_jar_failed_to_read_file))
            return null
        }
        val nearestRuntime = getNearestJreName(javaVersion)
        if (nearestRuntime == null) {
            finalErrorDialog("Você precisa reiniciar o aplicativo para continuar com a instalação..")
            return null
        }
        val selectedRuntime = MultiRTUtils.forceReread(nearestRuntime)
        val selectedJavaVersion =
            max(javaVersion.toDouble(), selectedRuntime.javaVersion.toDouble()).toInt()
        // Don't allow versions higher than Java 17 because our caciocavallo implementation does not allow for it
        if (selectedJavaVersion > 17) {
            finalErrorDialog(
                getString(
                    R.string.execute_jar_incompatible_runtime,
                    selectedJavaVersion
                )
            )
            return null
        }
        return selectedRuntime
    }

    private fun findModPath(argList: MutableList<String?>): File? {
        val argsSize = argList.size
        for (i in 0..<argsSize) {
            // Look for the -jar argument
            if (argList[i] != "-jar") continue
            val pathIndex = i + 1
            // Check if the supposed path is out of the argument bounds
            if (pathIndex >= argsSize) return null
            // Use the path as a file
            return File(argList[pathIndex])
        }
        return null
    }

    private fun startModInstaller(modFile: File?, javaArgs: String?) {
        Thread(Runnable {
            // Maybe replace with more advanced arg parsing logic later
            val argList = if (javaArgs != null) mutableListOf<String?>(
                *javaArgs.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()) else null
            var selectedMod = modFile
            if (selectedMod == null && argList != null) {
                // If modFile is not specified directly, try to extract the -jar argument from the javaArgs
                selectedMod = findModPath(argList)
            }
            val selectedRuntime: Runtime?
            if (selectedMod == null) {
                // We were unable to find out the path to the mod. In that case, use the default runtime.
                selectedRuntime = MultiRTUtils.forceReread(LauncherPreferences.PREF_DEFAULT_RUNTIME)
            } else {
                // Autoselect it properly in the other case.
                selectedRuntime = selectRuntime(selectedMod)
                // If the selection failed, just return. The autoselect function has already shown the dialog.
            }
            if (selectedRuntime == null) return@Runnable
            launchJavaRuntime(selectedRuntime, modFile, argList)
        }, "JREMainThread").start()
    }

    private fun finalErrorDialog(msg: CharSequence?) {
        runOnUiThread(Runnable {
            AlertDialog.Builder(this)
                .setTitle(R.string.global_error)
                .setMessage(msg)
                .setPositiveButton(
                    android.R.string.ok,
                    DialogInterface.OnClickListener { d: DialogInterface?, w: Int -> finish() })
                .setCancelable(false)
                .show()
        })
    }

    public override fun onResume() {
        super.onResume()
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        val decorView = getWindow().getDecorView()
        decorView.setSystemUiVisibility(uiOptions)
    }

    fun openLogOutput(v: View?) {
        mLoggerView!!.setVisibility(View.VISIBLE)
    }

    fun launchJavaRuntime(runtime: Runtime, modFile: File?, javaArgs: MutableList<String?>?) {
        JREUtils.redirectAndPrintJRELog()
        try {
            val javaArgList: MutableList<String?> = ArrayList<String?>()

            // Enable Caciocavallo
            Tools.getCacioJavaArgs(javaArgList, runtime.javaVersion == 8)
            if (javaArgs != null) {
                javaArgList.addAll(javaArgs)
            }
            if (modFile != null) {
                javaArgList.add("-jar")
                javaArgList.add(modFile.getAbsolutePath())
            }

            if (LauncherPreferences.PREF_JAVA_SANDBOX) {
                Collections.reverse(javaArgList)
                javaArgList.add("-Xbootclasspath/a:" + Tools.DIR_DATA + "/security/pro-grade.jar")
                javaArgList.add("-Djava.security.manager=net.sourceforge.prograde.sm.ProGradeJSM")
                javaArgList.add("-Djava.security.policy=" + Tools.DIR_DATA + "/security/java_sandbox.policy")
                Collections.reverse(javaArgList)
            }

            Logger.appendToLog(
                "Info: Java arguments: " + javaArgList.toTypedArray<String?>().contentToString()
            )

            JREUtils.launchJavaVM(
                this,
                runtime,
                null,
                javaArgList,
                LauncherPreferences.PREF_CUSTOM_JAVA_ARGS
            )
        } catch (th: Throwable) {
            th.printStackTrace()
            Tools.showError(this, th, true)
        }
    }

    fun getJavaVersion(modFile: File?): Int {
        try {
            ZipFile(modFile).use { zipFile ->
                val manifest = zipFile.getEntry("META-INF/MANIFEST.MF")
                if (manifest == null) return -1

                val manifestString = Tools.read(zipFile.getInputStream(manifest))
                var mainClass = Tools.extractUntilCharacter(manifestString, "Main-Class:", '\n')
                if (mainClass == null) return -1

                mainClass = mainClass.trim { it <= ' ' }.replace('.', '/') + ".class"
                val mainClassFile = zipFile.getEntry(mainClass)
                if (mainClassFile == null) return -1

                val classStream = zipFile.getInputStream(mainClassFile)
                val bytesWeNeed = ByteArray(8)
                val readCount = classStream.read(bytesWeNeed)
                classStream.close()
                if (readCount < 8) return -1

                val byteBuffer = ByteBuffer.wrap(bytesWeNeed)
                if (byteBuffer.getInt() != -0x35014542) return -1
                val minorVersion = byteBuffer.getShort()
                val majorVersion = byteBuffer.getShort()
                Log.i("JavaGUILauncher", majorVersion.toString() + "," + minorVersion)
                return classVersionToJavaVersion(majorVersion.toInt())
            }
        } catch (e: Exception) {
            Log.e("JavaVersion", "Exception thrown", e)
            return -1
        }
    }

    companion object {
        fun classVersionToJavaVersion(majorVersion: Int): Int {
            if (majorVersion < 46) return 2 // there isn't even an arm64 port of jre 1.1 (or anything before 1.8 in fact)

            return majorVersion - 44
        }
    }
}
