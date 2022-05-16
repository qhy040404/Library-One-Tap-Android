package com.qhy040404.libraryonetap

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import com.qhy040404.libraryonetap.constant.Constants
import rikka.preference.SimpleMenuPreference

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        when (getSharedPreferences(
            "com.qhy040404.libraryonetap_preferences",
            MODE_PRIVATE
        ).getString("dark", "system").toString()) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        when (getSharedPreferences(
            "com.qhy040404.libraryonetap_preferences",
            MODE_PRIVATE
        ).getString("theme", "purple").toString()) {
            "purple" -> setTheme(R.style.Theme_Purple)
            "blue" -> setTheme(R.style.Theme_Blue)
            "pink" -> setTheme(R.style.Theme_Pink)
            "green" -> setTheme(R.style.Theme_Green)
            "simple" -> setTheme(R.style.Theme_Simple)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<SimpleMenuPreference>(Constants.PREF_DARK)?.apply {
                setOnPreferenceChangeListener { _, _ ->
                    activity?.recreate()
                    true
                }
            }

            findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.apply {
                setOnPreferenceChangeListener { _, _ ->
                    activity?.recreate()
                    true
                }
            }

            findPreference<SimpleMenuPreference>(Constants.PREF_LOCALE)?.apply {
                setOnPreferenceChangeListener { _, _ ->
                    activity?.recreate()
                    true
                }
            }
        }
    }
}