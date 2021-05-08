plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "de.schnettler.scrobbler"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", true)
            }
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
        kotlinCompilerExtensionVersion = "1.0.0-beta03"
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
    implementation(project(":features:scrobble"))
    implementation(project(":features:search"))
    implementation(project(":features:settings"))
    implementation(project(":features:history"))
    implementation(project(":features:details"))

    // Libraries
    implementation(project(":libraries:submission"))
    implementation(project(":libraries:persistence"))

    implementation(project(":repo"))
    implementation(project(":ui:common:compose"))
    implementation(project(":ui:charts"))

    // AndroidX
    implementation(AndroidX.appCompat)
    implementation(AndroidX.compose.runtime.liveData)
    implementation("androidx.navigation:navigation-compose:_")
    implementation("androidx.activity:activity-compose:_")
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.lifecycleViewModel)
    implementation(AndroidX.hilt.work)
    implementation(project(mapOf("path" to ":features:profile")))
    implementation(project(mapOf("path" to ":libraries:image")))
    kapt(AndroidX.hilt.compiler)

    implementation(AndroidX.room.ktx)
    implementation(AndroidX.room.runtime)
    kapt(AndroidX.room.compiler)
    implementation("com.github.MatrixDev.Roomigrant:RoomigrantLib:0.2.0")
    kapt("com.github.MatrixDev.Roomigrant:RoomigrantCompiler:0.2.0")

    // Dagger
    implementation(Google.dagger.hilt.android)
    implementation("androidx.hilt:hilt-navigation-compose:1.0.0-alpha01")
    kapt(Google.dagger.hilt.android.compiler)

    // Other
    debugImplementation(Square.leakCanary.android)
}