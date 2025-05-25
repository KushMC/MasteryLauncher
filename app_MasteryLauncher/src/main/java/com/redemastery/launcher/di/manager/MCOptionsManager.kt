package com.redemastery.launcher.di.manager

import android.os.FileObserver
import android.util.Log
import com.redemastery.launcher.domain.model.MinecraftConfig
import com.redemastery.oldapi.pojav.Tools
import org.lwjgl.glfw.CallbackBridge.windowHeight
import org.lwjgl.glfw.CallbackBridge.windowWidth
import java.io.File
import java.io.FileWriter
import java.lang.ref.WeakReference

object MCOptionManager {

    private const val OPTIONS_FILENAME = "options.txt"
    private var optionsPath: String = Tools.DIR_GAME_NEW
    private var fileObserver: FileObserver? = null
    private val listeners = mutableListOf<WeakReference<MCOptionListener>>()

    var config: MinecraftConfig = MinecraftConfig()

    interface MCOptionListener {
        fun onOptionChanged()
    }

    fun load(path: String = Tools.DIR_GAME_NEW) {
        optionsPath = path
        val file = File("$optionsPath/$OPTIONS_FILENAME")
        if (!file.exists()) file.createNewFile()

        val map = mutableMapOf<String, String>()
        file.forEachLine { line ->
            val parts = line.split(":")
            if (parts.size >= 2) {
                val key = parts[0]
                val value = parts.drop(1).joinToString(":").trim()
                map[key] = value
            }
        }

        config = MinecraftConfig(
            renderDistance = map["renderDistance"]?.toIntOrNull() ?: 10,
            guiScale = map["guiScale"]?.toIntOrNull() ?: 2,
            fullscreen = map["fullscreen"]?.toBooleanStrictOrNull() == true,
            fov = map["fov"]?.toFloatOrNull() ?: 1.0f,
            gamma = map["gamma"]?.toFloatOrNull() ?: 1000.0f,
            mouseSensitivity = map["mouseSensitivity"]?.toFloatOrNull() ?: 0.5f,
            chatOpacity = map["chatOpacity"]?.toFloatOrNull() ?: 1.0f,
            chatScale = map["chatScale"]?.toFloatOrNull() ?: 1.0f,
            maxFps = map["maxFps"]?.toIntOrNull() ?: 260,
            enableVsync = map["enableVsync"]?.toBooleanStrictOrNull() == true,
            fancyGraphics = map["fancyGraphics"]?.toBooleanStrictOrNull() != false,
            particles = map["particles"]?.toIntOrNull() ?: 0,
            invertYMouse = map["invertYMouse"]?.toBooleanStrictOrNull() == true,
            lang = map["lang"] ?: "pt_BR",
            clouds = map["clouds"]?.toBooleanStrictOrNull() != false
        )

        setupFileObserver()
    }

    fun save() {
        val file = File("$optionsPath/$OPTIONS_FILENAME")
        try {
            fileObserver?.stopWatching()

            FileWriter(file).use { writer ->
                writer.write("renderDistance:${config.renderDistance}\n")
                writer.write("guiScale:${config.guiScale}\n")
                writer.write("fullscreen:${config.fullscreen}\n")
                writer.write("fov:${config.fov}\n")
                writer.write("gamma:${config.gamma}\n")
                writer.write("mouseSensitivity:${config.mouseSensitivity}\n")
                writer.write("chatOpacity:${config.chatOpacity}\n")
                writer.write("chatScale:${config.chatScale}\n")
                writer.write("maxFps:${config.maxFps}\n")
                writer.write("enableVsync:${config.enableVsync}\n")
                writer.write("fancyGraphics:${config.fancyGraphics}\n")
                writer.write("particles:${config.particles}\n")
                writer.write("invertYMouse:${config.invertYMouse}\n")
                writer.write("lang:${config.lang}\n")
                writer.write("clouds:${config.clouds}\n")
            }

            fileObserver?.startWatching()
        } catch (e: Exception) {
            Log.w(Tools.APP_NAME, "Erro ao salvar opções", e)
        }
    }

    fun getGuiScale(): Int {
        val guiScale = config.guiScale
        val scale = (windowWidth / 320).coerceAtMost(windowHeight / 240).coerceAtLeast(1)
        return if (guiScale == 0 || scale < guiScale) scale else guiScale
    }

    fun addListener(listener: MCOptionListener) {
        listeners.add(WeakReference(listener))
    }

    fun removeListener(listener: MCOptionListener) {
        listeners.removeAll { it.get() == listener || it.get() == null }
    }

    private fun notifyListeners() {
        listeners.removeAll { it.get() == null }
        listeners.forEach { it.get()?.onOptionChanged() }
    }

    private fun setupFileObserver() {
        fileObserver?.stopWatching()
        val filePath = "$optionsPath/$OPTIONS_FILENAME"

        fileObserver = object : FileObserver(filePath, MODIFY) {
            override fun onEvent(event: Int, path: String?) {
                if (event == MODIFY) {
                    load(optionsPath)
                    notifyListeners()
                }
            }
        }

        fileObserver?.startWatching()
    }
}
