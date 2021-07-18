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
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Modules
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:network:lastfm")) // TODO: This should not be here. Errors need to be mapped
    implementation(project(":libraries:model"))
    implementation(project(":libraries:submission"))

    // AndroidX
    implementation(KotlinX.coroutines.core)
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.lifecycle.service)
    implementation(AndroidX.lifecycle.runtimeKtx)

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)

    // DataStore
    implementation("com.github.Sh4dowSoul.ComposePreferences:datastore-manager:_")

    // Other
    implementation(JakeWharton.timber)
}