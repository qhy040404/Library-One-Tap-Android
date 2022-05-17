package com.qhy040404.libraryonetap.activity

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import java.util.*

abstract class StartUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val sp: SharedPreferences = getSharedPreferences(
            "com.qhy040404.libraryonetap_preferences",
            MODE_PRIVATE
        )
        val config = resources.configuration
        val dm = resources.displayMetrics

        when (sp.getString("dark", "system").toString()) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        when (sp.getString("theme", "simple").toString()) {
            "purple" -> setTheme(R.style.Theme_Purple)
            "blue" -> setTheme(R.style.Theme_Blue)
            "pink" -> setTheme(R.style.Theme_Pink)
            "green" -> setTheme(R.style.Theme_Green)
            "simple" -> setTheme(R.style.Theme_Simple)
        }
        when (sp.getString("locale", "system").toString()) {
            "system" -> {
                config.locale = Locale.getDefault()
            }
            "zh-Hans" -> {
                config.locale = Locale.SIMPLIFIED_CHINESE
            }
            "en" -> {
                config.locale = Locale.ENGLISH
            }
        }
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