import java.net.URI

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.gradle.enterprise") version "3.11.1"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        // Will be removed when rikkax.appcompat 1.5.0+ is pushed to mavenCentral
        maven {
            url = URI("https://s01.oss.sonatype.org/content/repositories/releases/")
            content {
                includeGroupByRegex("dev.rikka.*")
            }
        }
    }
}
rootProject.name = "Library-One-Tap"
include(":app")
