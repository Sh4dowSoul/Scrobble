import de.fayard.refreshVersions.core.versionFor

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(project(":libraries:core"))
    implementation(project(":libraries:model"))
    implementation(project(":libraries:compose"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:network:lastfm"))

    // Compose
    implementation(AndroidX.compose.material)
    implementation(AndroidX.compose.ui.tooling)
    implementation(Google.accompanist.insets)

    // Android X
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.room.ktx)
    implementation(AndroidX.paging.compose)

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.Moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    // Other
    implementation("com.dropbox.mobile.store", "store4", "_")
    implementation(JakeWharton.timber)

    // Test
    testImplementation(Testing.junit4)
    testImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
    testImplementation("app.cash.turbine", "turbine", "_")
}