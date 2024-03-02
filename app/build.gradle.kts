import java.net.InetAddress

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
  alias(libs.plugins.resopt)
}

val baseVersionName = "4.2.10"
val commitsCount by lazy { "git rev-list --count HEAD".exec().toInt() }

android {
  namespace = "com.qhy040404.libraryonetap"
  compileSdk = 34
  defaultConfig {
    applicationId = "com.qhy040404.libraryonetap"
    minSdk = 29
    targetSdk = 34
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
    sourceCompatibility(JavaVersion.VERSION_17)
    targetCompatibility(JavaVersion.VERSION_17)
  }
  kotlinOptions {
    jvmTarget = JavaVersion.VERSION_17.toString()
  }
  buildFeatures {
    buildConfig = true
    viewBinding = true
  }
}

configurations.all {
  exclude("androidx.appcompat", "appcompat")
  exclude("org.jetbrains.kotlin", "kotlin-stdlib-jdk7")
}

dependencies {
  implementation(libs.bundles.androidX)
  implementation(libs.bundles.rikkax)

  implementation(libs.drakeet.about)
  implementation(libs.drakeet.multitype)
  implementation(libs.datetime)
  implementation(libs.ab.utils)
  implementation(libs.material)
  implementation(libs.once)
  implementation(libs.moshi.kt)
  implementation(libs.okhttp)
  implementation(libs.okio)
  implementation(libs.prefx)
  implementation(libs.bugly)
  implementation(libs.coil)
  implementation(libs.cascade)
  implementation(libs.appiconloader.base)
  implementation(libs.appiconloader.coil)
  implementation(libs.coroutines)
  implementation(libs.kt.reflect)
  implementation(libs.spongycastle)
  implementation(libs.markdown)

  debugImplementation(libs.leakcanary)

  testImplementation(libs.junit)
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


fun String.exec(): String = exec(this)

@Suppress("UnstableApiUsage")
fun Project.exec(command: String): String = providers.exec {
  commandLine(command.split(" "))
}.standardOutput.asText.get().trim()
