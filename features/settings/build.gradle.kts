import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 24
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = versionFor(AndroidX.compose.ui)
    }
}

dependencies {
    // Libraries
    implementation(project(":libraries:compose")) // Only needed for MediaCardSize enum
    implementation(project(":libraries:persistence"))

    implementation("com.github.Sh4dowSoul.ComposePreferences:compose-preferences:_")

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
}