import java.util.Date
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "net.kdt.pojavlaunch"
    compileSdk = 34
    buildToolsVersion = "34.0.0"

    lint {
        abortOnError = false
    }

    signingConfigs {
        create("customDebug") {
            storeFile = file("debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
        create("googlePlayBuild") {
            storeFile = file("upload.jks")
            storePassword = System.getenv("GPLAY_KEYSTORE_PASSWORD") ?: ""
            keyAlias = "upload"
            keyPassword = System.getenv("GPLAY_KEYSTORE_PASSWORD") ?: ""
        }
    }

    defaultConfig {
        applicationId = "net.kdt.pojavlaunch"
        minSdk = 21
        targetSdk = 34
        versionCode = getDateSeconds()
        versionName = getVersionName()
        multiDexEnabled = true
        resValue("string", "curseforge_api_key", getCFApiKey())
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("customDebug")
            resValue("string", "application_package", "net.kdt.pojavlaunch.debug")
            resValue("string", "storageProviderAuthorities", "net.kdt.pojavlaunch.scoped.gamefolder.debug")
            resValue("string", "shareProviderAuthority", "net.kdt.pojavlaunch.scoped.controlfolder.debug")
        }

        create("proguard") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
            isShrinkResources = true
        }

        create("proguardNoDebug") {
            initWith(getByName("proguard"))
            isDebuggable = false
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "storageProviderAuthorities", "net.kdt.pojavlaunch.scoped.gamefolder")
            resValue("string", "application_package", "net.kdt.pojavlaunch")
        }

        create("gplay") {
            initWith(getByName("release"))
            signingConfig = signingConfigs.getByName("googlePlayBuild")
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    packaging {
        jniLibs {
            useLegacyPackaging = true
        }
        resources {
            pickFirsts += "**/libbytehook.so"
        }
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        viewBinding = true
        prefab = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
}

fun getDate(): String = SimpleDateFormat("yyyyMMdd").format(Date())

fun getDateSeconds(): Int {
    return if (System.getenv("GITHUB_ACTIONS") == "true") {
        9934841 + (System.getenv("GITHUB_RUN_NUMBER")?.toInt() ?: 0)
    } else {
        172005
    }
}

fun getVersionName(): String {
    val tagOutputStream = ByteArrayOutputStream()
    val branchOutputStream = ByteArrayOutputStream()
    val tagPartCommitOutputStream = ByteArrayOutputStream()

    try {
        project.exec {
            commandLine("git", "describe", "--tags")
            isIgnoreExitValue = true
            standardOutput = tagOutputStream
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    var tagString = tagOutputStream.toString().trim()

    if (tagString.isEmpty()) {
        try {
            project.exec {
                commandLine("git", "describe", "--always", "--tags")
                isIgnoreExitValue = true
                standardOutput = tagPartCommitOutputStream
            }
        } catch (e: Exception) { /* Ignored */ }
    }

    if (tagString.isEmpty()) {
        val tagPartCommit = tagPartCommitOutputStream.toString().trim()
        tagString = if (tagPartCommit.isEmpty()) {
            return "LOCAL-${getDate()}"
        } else {
            "gladiolus-${getDate()}-$tagPartCommit"
        }
    }

    try {
        project.exec {
            commandLine("git", "branch", "--show-current")
            isIgnoreExitValue = true
            standardOutput = branchOutputStream
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    val branch = branchOutputStream.toString().trim()
    return tagString.replace("-g", "-") + "-$branch"
}

fun getCFApiKey(): String {
    System.getenv("CURSEFORGE_API_KEY")?.let { return it }

    val curseforgeKeyFile = File("./curseforge_key.txt")
    if (curseforgeKeyFile.canRead() && curseforgeKeyFile.isFile) {
        return curseforgeKeyFile.readText()
    }

    project.logger.warn("BUILD: You have no CurseForge key, the curseforge api will get disabled !")
    return "DUMMY"
}

configurations {
    create("instrumentedClasspath") {
        isCanBeConsumed = false
        isCanBeResolved = true
    }
}

project.afterEvaluate {
    tasks.named("mergeDebugAssets") {
        dependsOn(
                ":forge_installer:jar",
                ":arc_dns_injector:jar",
                ":jre_lwjgl3glfw:jar"
        )
    }
}

dependencies {
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("commons-codec:commons-codec:1.15")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("com.github.duanhong169:checkerboarddrawable:1.0.2")
    implementation("com.github.PojavLauncherTeam:portrait-sdp:ed33e89cbc")
    implementation("com.github.PojavLauncherTeam:portrait-ssp:6c02fd739b")
    implementation("com.github.Mathias-Boulay:ExtendedView:1.0.0")
    implementation("com.github.Mathias-Boulay:android_gamepad_remapper:2.0.3")
    implementation("com.github.Mathias-Boulay:virtual-joystick-android:1.14")

    implementation("org.tukaani:xz:1.8")
    implementation("net.sourceforge.htmlcleaner:htmlcleaner:2.6.1")
    implementation("com.bytedance:bytehook:1.0.9")

    // AndroidX
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.concurrent.futures)
    implementation(libs.androidx.material2)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.common)
    implementation(libs.hilt.navigation)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)


    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}