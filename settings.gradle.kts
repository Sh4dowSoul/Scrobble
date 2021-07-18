include(":features:charts")
include(":features:profile")
include(":features:details")
include(":features:history")
include(":features:search")
include(":features:settings")
include(":features:scrobble")

include(":libraries:authentication")
include(":libraries:image")
include(":libraries:submission")
include(":libraries:persistence")
include(":libraries:network:common")
include(":libraries:network:spotify")
include(":libraries:network:lastfm")
include(":libraries:model")
include(":libraries:core")
include(":libraries:compose")
include(":libraries:resources")

include(":app")

rootProject.name = "Scrobbler"

pluginManagement {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
        gradlePluginPortal()
        jcenter()
        google()
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "dagger.hilt.android.plugin" ->
                    useModule("com.google.dagger:hilt-android-gradle-plugin:2.28-alpha:${requested.version}")
            }
        }
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.10.1"
}

buildscript {
    repositories {
        gradlePluginPortal()
    }
}