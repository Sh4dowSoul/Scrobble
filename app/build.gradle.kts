import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30

    defaultConfig {
        applicationId = "de.schnettler.scrobbler"
        minSdk = 24
        targetSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        versionCode = 1
        versionName = "1.0"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", true)
            }
        }

        sourceSets {
            getByName("androidTest").assets.srcDirs("$projectDir/schemas")
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.ui)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs += listOf("-Xskip-prerelease-check")
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("debug.keystore")
        }
    }

    packagingOptions {
        resources {
            excludes.apply {
                add("/META-INF/AL2.0")
                add("/META-INF/LGPL2.1")
            }
        }
    }

    kapt {
        correctErrorTypes = true
    }
}

hilt {
    enableExperimentalClasspathAggregation = true
}

dependencies {
    // Features
    implementation(project(":features:search"))
    implementation(project(":features:charts"))
    implementation(project(":features:history"))
    implementation(project(":features:details"))
    implementation(project(":features:profile"))
    implementation(project(":features:scrobble"))
    implementation(project(":features:settings"))

    // Libraries
    implementation(project(":libraries:core"))
    implementation(project(":libraries:image"))
    implementation(project(":libraries:model"))
    implementation(project(":libraries:compose"))
    implementation(project(":libraries:submission"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:authentication"))
    implementation(project(":libraries:network:lastfm"))

    // Compose
    implementation(AndroidX.appCompat)
    implementation(AndroidX.compose.material)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.activity.compose)
    implementation(AndroidX.hilt.navigationCompose)
    implementation(Google.accompanist.insets)
    implementation(Google.accompanist.insets.ui)

    // AndroidX
    implementation(AndroidX.compose.runtime.liveData)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.work)
    implementation(AndroidX.browser)
    kapt(AndroidX.hilt.compiler)
    implementation(AndroidX.room.ktx)
    implementation(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)
    implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:0.2.0")
    kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:0.2.0")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    // Other
    debugImplementation(Square.leakCanary.android)
    implementation(JakeWharton.timber)

    // Android Test
    androidTestImplementation(Testing.junit4)
    androidTestImplementation(KotlinX.coroutines.test)
    androidTestImplementation(AndroidX.test.ext.junitKtx)
    androidTestImplementation(AndroidX.archCore.testing)
    androidTestImplementation(AndroidX.test.rules)
    androidTestImplementation(AndroidX.room.testing)
    androidTestImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}