package com.qhy040404.libraryonetap.ui.about

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import coil.load
import com.drakeet.about.Category
import com.drakeet.about.Contributor
import com.drakeet.about.License
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.PackageUtils
import com.qhy040404.libraryonetap.utils.extensions.getStringAndFormat
import com.qhy040404.libraryonetap.utils.extensions.showToast
import com.qhy040404.libraryonetap.utils.extensions.start

@Suppress("SpellCheckingInspection")
class AboutActivity : AbsAboutActivityProxy() {
  private var headCount = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_MaterialComponents_DayNight_NoActionBar_Library)

    super.onCreate(savedInstanceState)
    LibraryOneTapApp.instance?.addActivity(this)

    findViewById<Toolbar>(com.drakeet.about.R.id.toolbar)?.background = null

    val color = getColor(
      if (AppUtils.currentIsNightMode(this)) {
        R.color.about_black
      } else {
        R.color.library_500
      }
    )
    setHeaderBackground(ColorDrawable(color))
    setHeaderContentScrim(ColorDrawable(color))
  }

  @SuppressLint("SourceLockedOrientationActivity")
  override fun onResume() {
    super.onResume()
    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
  }

  override fun onDestroy() {
    super.onDestroy()
    LibraryOneTapApp.instance?.removeActivity(this)
  }

  override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
    icon.load(R.drawable.ic_about_foreground)
    slogan.text = getString(R.string.about_slogan)
    version.text = R.string.about_version.getStringAndFormat(
      PackageUtils.buildType,
      PackageUtils.versionName,
      PackageUtils.versionCode
    )

    icon.setOnClickListener {
      headCount++
      when (headCount) {
        1 -> showToast("点我干啥？")
        in 2..9 -> {}
        10 -> {
          slogan.text = MIDDLE_SECRET
          showToast("点坏了啊喂！")
        }

        in 11..19 -> {}
        20 -> {
          slogan.text = SECRET
          showToast("你有本事继续点")
        }

        else -> URLManager.SURPRISE_BILI.toUri().start(this) {
          URLManager.SURPRISE_HTTP.toUri().start(this)
        }
      }
    }
  }

  override fun onItemsCreated(items: MutableList<Any>) {
    items.apply {
      add(
        Contributor(
          R.mipmap.dcmt,
          "别卷了，行不行",
          "给我点个Star吧秋梨膏",
          URLManager.GITHUB_REPO
        )
      )

      add(Category("Developer"))
      add(
        Contributor(
          R.mipmap.qhy040404_avatar,
          "qhy040404",
          URLManager.GITHUB_PAGE,
          URLManager.GITHUB_PAGE
        )
      )
      add(
        Contributor(
          R.drawable.ic_github,
          "Source Code",
          URLManager.GITHUB_REPO,
          URLManager.GITHUB_REPO
        )
      )

      add(Category("Open Source Licenses"))
      add(
        License(
          "kotlin",
          "JetBrains",
          License.APACHE_2,
          "https://github.com/JetBrains/kotlin"
        )
      )
      add(
        License(
          "MultiType",
          "drakeet",
          License.APACHE_2,
          "https://github.com/drakeet/MultiType"
        )
      )
      add(
        License(
          "about-page",
          "drakeet",
          License.APACHE_2,
          "https://github.com/drakeet/about-page"
        )
      )
      add(
        License(
          "AndroidX",
          "Google",
          License.APACHE_2,
          "https://source.google.com"
        )
      )
      add(
        License(
          "Android Jetpack",
          "Google",
          License.APACHE_2,
          "https://source.google.com"
        )
      )
      add(
        License(
          "material-components-android",
          "Google",
          License.APACHE_2,
          "https://github.com/material-components/material-components-android"
        )
      )
      add(
        License(
          "RikkaX",
          "RikkaApps",
          License.MIT,
          "https://github.com/RikkaApps/RikkaX"
        )
      )
      add(
        License(
          "OkHttp",
          "Square",
          License.APACHE_2,
          "https://github.com/square/okhttp"
        )
      )
      add(
        License(
          "coil",
          "coil-kt",
          License.APACHE_2,
          "https://github.com/coil-kt/coil"
        )
      )
      add(
        License(
          "Moshi",
          "Square",
          License.APACHE_2,
          "https://github.com/square/moshi"
        )
      )
      add(
        License(
          "libraries",
          "zhaobozhen",
          License.MIT,
          "https://github.com/zhaobozhen/libraries"
        )
      )
      add(
        License(
          "AppIconLoader",
          "zhanghai",
          License.APACHE_2,
          "https://github.com/zhanghai/AppIconLoader"
        )
      )
      add(
        License(
          "Okio",
          "Square",
          License.APACHE_2,
          "https://github.com/square/okio"
        )
      )
      add(
        License(
          "PreferenceX",
          "takisoft",
          License.APACHE_2,
          "https://github.com/takisoft/preferencex-android"
        )
      )
      add(
        License(
          "Once",
          "jonfinerty",
          License.APACHE_2,
          "https://github.com/jonfinerty/Once"
        )
      )
    }
  }

  companion object {
    private const val MIDDLE_SECRET = "ne Tap Ag"
    private const val SECRET = "Tap Again"
  }
}
