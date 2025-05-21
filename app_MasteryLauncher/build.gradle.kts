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
    namespace = "com.redemastery.launcher"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    lint {
        abortOnError = false
    }

    defaultConfig {
        applicationId = "com.redemastery.launcher"
        minSdk = 27
        targetSdk = 35
        versionCode = 10
        versionName = "MasteryLauncher 1.0"
        multiDexEnabled = true
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
            resValue("string", "application_package", "com.redemastery.launcher.debug")
            resValue("string", "storageProviderAuthorities", "com.redemastery.oldapi.pojav.scoped.gamefolder.debug")
            resValue("string", "shareProviderAuthority", "com.redemastery.oldapi.pojav.scoped.controlfolder.debug")
        }

        create("proguard") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
            isShrinkResources = false
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
            resValue("string", "storageProviderAuthorities", "com.redemastery.oldapi.pojav.scoped.gamefolder")
            resValue("string", "application_package", "com.redemastery.oldapi.pojav")
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
        kotlinCompilerExtensionVersion = "1.5.7"
    }
}

fun getDate(): String = SimpleDateFormat("yyyyMMdd").format(Date())

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

    implementation(project(":design"))

    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("commons-codec:commons-codec:1.15")
    implementation("androidx.preference:preference:1.2.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.annotation:annotation:1.5.0")

    implementation("com.github.duanhong169:checkerboarddrawable:1.0.2")
    implementation("com.github.PojavLauncherTeam:portrait-sdp:ed33e89cbc")
    implementation("com.github.PojavLauncherTeam:portrait-ssp:6c02fd739b")
    implementation("com.github.Mathias-Boulay:ExtendedView:1.0.0")
    implementation("com.github.Mathias-Boulay:android_gamepad_remapper:2.0.3")
    implementation("com.github.Mathias-Boulay:virtual-joystick-android:1.14")

    implementation("org.tukaani:xz:1.8")
    implementation("net.sourceforge.htmlcleaner:htmlcleaner:2.6.1")
    implementation("com.bytedance:bytehook:1.0.9")

    implementation("io.coil-kt:coil-gif:2.4.0")
    implementation(libs.coil.compose)
    testImplementation(libs.junit)

    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.stdlib.jdk7)
    implementation(libs.kotlinx.coroutines.android)
    // AndroidX
    implementation(libs.androidx.appcompat)
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
    implementation(libs.androidx.material.default)
    implementation(libs.androidx.activity)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.accompanist.permissions)
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

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
}