@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import java.net.InetAddress
import java.nio.charset.Charset
import java.nio.file.Paths

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val baseVersionName = "4.0.1"
val commitsCount by lazy { "git rev-list --count HEAD".exec().toInt() }

android {
    namespace = "com.qhy040404.libraryonetap"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.qhy040404.libraryonetap"
        minSdk = 29
        targetSdk = 33
        versionCode = commitsCount
        versionName = baseVersionName

        resourceConfigurations.addAll(
            setOf(
                "en",
                "zh-rCN"
            )
        )

        base.archivesName.set("Library-One-Tap_v$versionName")
        manifestPlaceholders["BUILD_HOST"] = getBuildHost()
        manifestPlaceholders["CHANNEL"] = getBuildType(false)
        manifestPlaceholders["COMMIT"] = getGitCommitHash()
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isCrunchPngs = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk.abiFilters.add("")
            packagingOptions.resources.excludes += setOf(
                "DebugProbesKt.bin",
                "META-INF/*.version"
            )
            dependenciesInfo.includeInApk = false
        }

        all {
            buildConfigField("String", "CHANNEL", getBuildType(true))
            buildConfigField("String", "BUGLY_APPID", getBuglyAppID(true))
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        viewBinding = true
    }
}

configurations.all {
    exclude("androidx.appcompat", "appcompat")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
}

dependencies {
    implementation("androidx.activity:activity-ktx:1.6.1")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.browser:browser:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("com.drakeet.about:about:2.5.2")
    implementation("com.drakeet.multitype:multitype:4.3.0")
    implementation("com.github.qhy040404:datetime:1.2.6")
    implementation("com.github.zhaobozhen.libraries:utils:1.1.3")
    implementation("com.google.android.material:material:1.7.0")
    implementation("com.jonathanfinerty.once:once:1.3.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okio:okio:3.3.0")
    implementation("com.takisoft.preferencex:preferencex:1.1.0")
    implementation("com.tencent.bugly:crashreport:4.1.9")
    implementation("dev.rikka.rikkax.appcompat:appcompat:1.5.0.1")
    implementation("dev.rikka.rikkax.material:material:2.5.1")
    implementation("dev.rikka.rikkax.material:material-preference:2.0.0")
    implementation("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
    implementation("dev.rikka.rikkax.recyclerview:recyclerview-ktx:1.3.1")
    implementation("dev.rikka.rikkax.widget:borderview:1.1.0")
    implementation("io.coil-kt:coil:2.2.2")
    implementation("me.zhanghai.android.appiconloader:appiconloader:1.5.0")
    implementation("me.zhanghai.android.appiconloader:appiconloader-coil:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // To unify version
    implementation("androidx.collection:collection-ktx:1.2.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.5.1")
    implementation("androidx.lifecycle:lifecycle-process:2.5.1")
    implementation("androidx.lifecycle:lifecycle-service:2.5.1")
    implementation("androidx.transition:transition:1.4.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.10")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.10")

    testImplementation("junit:junit:4.13.2")
}

tasks.matching {
    it.name.contains("optimizeReleaseRes")
}.configureEach {
    doLast {
        val aapt2 = File(
            androidComponents.sdkComponents.sdkDirectory.get().asFile,
            "build-tools/${project.android.buildToolsVersion}/aapt2"
        )
        val zip = Paths.get(
            buildDir.path,
            "intermediates",
            "optimized_processed_res",
            "release",
            "resources-release-optimize.ap_"
        )
        val optimized = File("$zip.opt")
        val cmd = exec {
            commandLine(
                aapt2, "optimize",
                "--collapse-resource-names",
                "--resources-config-path", "aapt2-resources.cfg",
                "-o", optimized,
                zip
            )
            isIgnoreExitValue = false
        }
        if (cmd.exitValue == 0) {
            delete(zip)
            optimized.renameTo(zip.toFile())
        }
    }
}

fun getBuglyAppID(isBuildConfig: Boolean): String {
    return if (!isBuildConfig) {
        System.getenv("BUGLY_APPID")
    } else {
        "\"${getBuglyAppID(false)}\""
    }
}

fun getBuildType(isBuildConfig: Boolean): String {
    return if (!isBuildConfig) {
        if ("git tag -l $baseVersionName".exec().isNotEmpty()
            && getStartParameters().contains("Release")
        ) {
            "Release"
        } else if ("git tag -l $baseVersionName-Pre".exec().isNotEmpty()
            && getStartParameters().contains("Release")
        ) {
            "Pre-release"
        } else {
            "Debug"
        }
    } else {
        "\"${getBuildType(false)}\""
    }
}

fun getBuildHost() = InetAddress.getLocalHost().hostName

fun getGitCommitHash(): String = "git rev-parse HEAD".exec()

fun getStartParameters(): String =
    gradle.startParameter.taskRequests[0].args.getOrElse(0) { "test" }

fun String.exec(): String = Runtime.getRuntime().exec(this).inputStream.readBytes()
    .toString(Charset.defaultCharset()).trim()
