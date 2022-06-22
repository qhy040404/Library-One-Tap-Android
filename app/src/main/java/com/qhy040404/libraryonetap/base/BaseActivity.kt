package com.qhy040404.libraryonetap.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.RandomDataUtils
import java.util.*

@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val config = resources.configuration
        val dm = resources.displayMetrics
        var theme = GlobalValues.theme

        when (GlobalValues.darkMode) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        if (GlobalValues.theme == "random") {
            theme = RandomDataUtils.randomTheme
        }
        when (theme) {
            "purple" -> setTheme(R.style.Theme_Purple)
            "blue" -> setTheme(R.style.Theme_Blue)
            "pink" -> setTheme(R.style.Theme_Pink)
            "green" -> setTheme(R.style.Theme_Green)
            "orange" -> setTheme(R.style.Theme_Orange)
            "red" -> setTheme(R.style.Theme_Red)
            "simple" -> setTheme(R.style.Theme_Simple)
        }
        config.setLocale(
            when (GlobalValues.locale) {
                "zh" -> Locale.SIMPLIFIED_CHINESE
                "en" -> Locale.ENGLISH
                else -> Locale.getDefault()
            }
        )
        resources.updateConfiguration(config, dm)

        super.onCreate(savedInstanceState)
        LibraryOneTapApp.instance?.addActivity(this)

        setContentView(getLayoutId())

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun init()

    protected abstract fun getLayoutId(): Int
}