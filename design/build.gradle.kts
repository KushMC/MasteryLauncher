plugins {
    id("com.android.library")
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.redemastery.design"
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
            isShrinkResources = false
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
    implementation("io.coil-kt:coil-gif:2.4.0")

    implementation(libs.coil.compose)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}