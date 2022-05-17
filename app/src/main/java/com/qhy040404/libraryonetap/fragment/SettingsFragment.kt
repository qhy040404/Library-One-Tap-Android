package com.qhy040404.libraryonetap.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import rikka.preference.SimpleMenuPreference

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