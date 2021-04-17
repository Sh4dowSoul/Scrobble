plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(21)
    }
    buildFeatures {
        compose = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.0.0-beta02"
    }
}

dependencies {
    implementation(project(":ui:common:util"))
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))

    implementation(project(":ui:common:compose"))
    implementation(project(":ui:common:resources"))
    implementation(project(":repo"))

    api("androidx.datastore:datastore-preferences:_")
}