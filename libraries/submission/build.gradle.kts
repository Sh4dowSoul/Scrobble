plugins {
    id("com.android.library")
    id("kotlin-android")
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
    implementation(project(":libraries:model"))
    implementation(project(":libraries:network:common"))
    implementation(project(":libraries:network:lastfm"))
    implementation(project(":libraries:persistence"))
    implementation(project(":libraries:core"))

    // Network & Serialization
    implementation(Square.Retrofit2.retrofit)
    implementation(Square.moshi.kotlinCodegen)
    implementation("com.serjltt.moshi", "moshi-lazy-adapters", "_")

    // Repository
    implementation("com.dropbox.mobile.store", "store4", "_")
    implementation("com.github.Sh4dowSoul.ComposePreferences:datastore-manager:_")

    // AndroidX
    implementation(AndroidX.work.runtimeKtx)
    implementation(AndroidX.hilt.work)
    implementation(AndroidX.room.common)

    // Dagger
    implementation(Google.dagger.hilt.android)
    kapt(Google.dagger.hilt.compiler)
    kapt(AndroidX.hilt.compiler)

    // Other
    implementation(JakeWharton.timber)

    // Test
    testImplementation(Testing.junit4)
    testImplementation("ch.tutteli.atrium", "atrium-fluent-en_GB", "_")
}