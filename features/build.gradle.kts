plugins {
    id("com.android.library")
    id("kotlin-kapt")
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.choice.features"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }



    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            resValue("string", "application_package", "com.redemastery.oldapi.pojav.debug")
            resValue("string", "storageProviderAuthorities", "com.redemastery.oldapi.pojav.scoped.gamefolder.debug")
            resValue("string", "shareProviderAuthority", "com.redemastery.oldapi.pojav.scoped.controlfolder.debug")
        }

        create("proguard") {
            initWith(getByName("debug"))
            isMinifyEnabled = true
        }

        create("proguardNoDebug") {
            initWith(getByName("proguard"))
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

        create("gplay") {
            initWith(getByName("release"))

        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation(project(":design"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.hilt.android)
    implementation(libs.hilt.common)
    implementation(libs.hilt.navigation)
    kapt(libs.hilt.android.compiler)
    kapt(libs.hilt.compiler)
}