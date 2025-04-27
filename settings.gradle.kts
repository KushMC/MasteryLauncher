pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

rootProject.name = "PojavLauncher"

include(":jre_lwjgl3glfw")
include(":app_pojavlauncher")
include(":arc_dns_injector")
include(":forge_installer")