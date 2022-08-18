@file:Suppress("SpellCheckingInspection")

import java.net.InetAddress
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val baseVersionName = "3.3.5"
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

        base.archivesName.set("Library-One-Tap_v$versionName")
        manifestPlaceholders["CHANNEL"] = getBuildType()
        manifestPlaceholders["BUILD_TIME"] = getBuildTime()
        manifestPlaceholders["BUILD_HOST"] = getBuildHost()
        manifestPlaceholders["COMMIT"] = getGitCommitHash()
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }

        release {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            ndk {
                abiFilters.add("arm64-v8a")
            }
        }

        all {
            buildConfigField("String", "CHANNEL", "\"${getBuildType()}\"")
            buildConfigField("String", "BUGLY_APPID", "\"${getBuglyAppID()}\"")
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
        dataBinding = true
    }
}

configurations.all {
    exclude("androidx.appcompat", "appcompat")
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.0-beta01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.core:core-splashscreen:1.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.1")
    implementation("androidx.preference:preference:1.2.0")
    implementation("com.drakeet.about:about:2.5.1")
    implementation("com.drakeet.multitype:multitype:4.3.0")
    implementation("com.github.zhaobozhen.libraries:utils:1.1.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation("com.squareup.moshi:moshi-kotlin:1.13.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okio:okio:3.2.0")
    implementation("com.takisoft.preferencex:preferencex:1.1.0")
    implementation("com.tencent.bugly:crashreport:4.0.4")
    implementation("dev.rikka.rikkax.appcompat:appcompat:1.5.0")
    implementation("dev.rikka.rikkax.material:material:2.5.1")
    implementation("dev.rikka.rikkax.material:material-preference:2.0.0")
    implementation("dev.rikka.rikkax.preference:simplemenu-preference:1.0.3")
    implementation("dev.rikka.rikkax.recyclerview:recyclerview-ktx:1.3.1")
    implementation("dev.rikka.rikkax.widget:borderview:1.1.0")
    implementation("io.coil-kt:coil:2.2.0")
    implementation("me.zhanghai.android.appiconloader:appiconloader:1.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    testImplementation("junit:junit:4.13.2")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.21.0")
}

fun getBuglyAppID(): String = System.getenv("BUGLY_APPID")

fun getBuildTime(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    formatter.timeZone = TimeZone.getTimeZone("\"GMT+08:00\"")
    return formatter.format(Date())
}

fun getBuildType(): String {
    return if ("git tag -l $baseVersionName".exec().isNotEmpty()) "Release"
    else if ("git tag -l $baseVersionName-Pre".exec().isNotEmpty()) "Pre-release"
    else "Debug"
}

fun getBuildHost(): String = InetAddress.getLocalHost().hostName

fun getGitCommitHash(): String = "git rev-parse HEAD".exec()

fun String.exec(): String = Runtime.getRuntime().exec(this).inputStream.readBytes()
    .toString(Charset.defaultCharset()).trim()
