@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
  }
}

plugins {
  id("com.gradle.develocity") version "3.17.4"
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven("https://jitpack.io")
  }
}

develocity {
  buildScan {
    termsOfUseUrl = "https://gradle.com/terms-of-service"
    termsOfUseAgree = "yes"
    // TODO: workaround for https://github.com/gradle/gradle/issues/22879.
    val isCI = providers.environmentVariable("CI").isPresent
    publishing.onlyIf { isCI }
  }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Library-One-Tap"
include(":app")
