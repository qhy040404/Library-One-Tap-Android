@file:Suppress("SpellCheckingInspection")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.3.0-rc01" apply false
    id("com.android.library") version "7.3.0-rc01" apply false
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}