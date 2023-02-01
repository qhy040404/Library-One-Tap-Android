@file:Suppress("SpellCheckingInspection")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val agpVersion = "7.4.1"

    id("com.android.application") version agpVersion apply false
    id("com.android.library") version agpVersion apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
