import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.library")
    id("kotlin-android")
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
    // Modules
    implementation(project(":libraries:core"))
    implementation(project(":libraries:model"))

    // AndroidX
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.paletteKtx)

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.material.icons.extended)
    implementation(AndroidX.compose.ui.tooling)
    implementation(AndroidX.navigation.compose)
    implementation(AndroidX.constraintLayoutCompose)
    implementation(Google.accompanist.insets)
    implementation(Google.accompanist.pager)
    implementation(Google.accompanist.flowlayout)
    implementation(Google.accompanist.swiperefresh)
    implementation(Google.accompanist.insets.ui)
    implementation("com.google.accompanist", "accompanist-pager-indicators", "_")

    // Other
    implementation(JakeWharton.timber)
    implementation("io.coil-kt", "coil-compose", "_")
}