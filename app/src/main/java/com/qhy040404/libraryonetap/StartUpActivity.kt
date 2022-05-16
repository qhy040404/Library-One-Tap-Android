package com.qhy040404.libraryonetap

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
        when (sp.getString("theme", "purple").toString()) {
            "purple" -> setTheme(R.style.Theme_Purple)
            "blue" -> setTheme(R.style.Theme_Blue)
            "pink" -> setTheme(R.style.Theme_Pink)
            "green" -> setTheme(R.style.Theme_Green)
            "simple" -> setTheme(R.style.Theme_Simple)
        }
        super.onCreate(savedInstanceState)

        setContentView(getLayoutId())
        init()
    }

    protected abstract fun init()

    protected abstract fun getLayoutId(): Int
}