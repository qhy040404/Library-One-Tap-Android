// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.kotlinter) apply false
  alias(libs.plugins.resopt) apply false
}

allprojects {
  apply(plugin = rootProject.libs.plugins.kotlinter.get().pluginId)
}

task<Delete>("clean") {
  delete(rootProject.layout.buildDirectory)
}
