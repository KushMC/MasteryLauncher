package com.redemastery.launcher.domain.model

data class MinecraftConfig(
    var renderDistance: Int = 10,
    var guiScale: Int = 2,
    var fullscreen: Boolean = false,
    var fov: Float = 1.0f,
    var gamma: Float = 1000.0f,
    var mouseSensitivity: Float = 0.5f,
    var chatOpacity: Float = 1.0f,
    var chatScale: Float = 1.0f,
    var maxFps: Int = 260,
    var enableVsync: Boolean = false,
    var fancyGraphics: Boolean = true,
    var particles: Int = 0,
    var invertYMouse: Boolean = false,
    var lang: String = "pt_BR",
    var clouds: Boolean = true
)
