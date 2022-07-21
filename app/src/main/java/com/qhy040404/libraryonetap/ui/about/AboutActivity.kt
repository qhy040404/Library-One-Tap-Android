package com.qhy040404.libraryonetap.ui.about

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import coil.load
import com.drakeet.about.Category
import com.drakeet.about.Contributor
import com.drakeet.about.License
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.ui.secret.SecretActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.PackageUtils

@Suppress("SpellCheckingInspection")
class AboutActivity : AbsAboutActivityProxy() {
    private var headCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.load(R.drawable.ic_about_foreground)
        slogan.text = getString(R.string.about_slogan)
        version.text = String.format(
            "%s v%s (%d)",
            PackageUtils.buildType,
            PackageUtils.versionName,
            PackageUtils.versionCode
        )

        icon.setOnClickListener {
            headCount++
            when (headCount) {
                1 -> Toast.makeText(this, "咋还想点呢", Toast.LENGTH_SHORT).show()
                in 2..9 -> {}
                10 -> {
                    slogan.text = MIDDLE_SECRET
                    Toast.makeText(this, "点坏了啊喂！", Toast.LENGTH_SHORT).show()
                }
                in 11..19 -> {}
                20 -> slogan.text = SECRET
                else -> startActivity(Intent(this, SecretActivity::class.java))
            }
        }
    }

    override fun onItemsCreated(items: MutableList<Any>) {
        items.apply {
            add(Contributor(R.mipmap.dcmt, "别卷了，行不行", "去下面给我点个Star"))

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
        }
    }

    private fun initView() {
        LibraryOneTapApp.instance?.addActivity(this)

        findViewById<Toolbar>(com.drakeet.about.R.id.toolbar)?.background = null

        val color = getColor(if (AppUtils.currentIsNightMode(this)) {
            R.color.black
        } else {
            R.color.library_500
        })
        setHeaderBackground(ColorDrawable(color))
        setHeaderContentScrim(ColorDrawable(color))
    }

    companion object {
        private const val MIDDLE_SECRET = "ne Tap D"
        private const val SECRET = "Tap Dog"
    }
}