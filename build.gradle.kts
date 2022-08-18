@file:Suppress("SpellCheckingInspection")

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.2.2" apply false
    id("com.android.library") version "7.2.2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.21.0" apply true
    id("org.jetbrains.kotlin.android") version "1.7.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0" apply true
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
