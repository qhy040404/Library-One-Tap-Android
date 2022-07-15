package com.qhy040404.libraryonetap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.ui.about.AboutActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import rikka.material.app.DayNightDelegate
import rikka.material.app.LocaleDelegate
import rikka.preference.SimpleMenuPreference
import java.util.*
import kotlin.system.exitProcess

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SimpleMenuPreference>(Constants.PREF_DARK)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                GlobalValues.darkMode = newValue.toString()
                DayNightDelegate.setDefaultNightMode(AppUtils.getNightMode(newValue.toString()))
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_LOCALE)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val locale: Locale = if ("SYSTEM" == newValue) {
                        LocaleDelegate.systemLocale
                    } else {
                        Locale.forLanguageTag(newValue)
                    }
                    LocaleDelegate.defaultLocale = locale
                    activity?.recreate()
                }
                true
            }
        }

        findPreference<Preference>(Constants.PREF_RESET)?.apply {
            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.dataResetConfirm)
                    .setTitle(R.string.title_activity_settings)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        SPUtils.resetAll()
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.dataResetFinish)
                            .setTitle(R.string.title_activity_settings)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                LibraryOneTapApp.instance?.exit()
                                exitProcess(0)
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                    .setNegativeButton(R.string.no) { _, _ -> }
                    .create()
                    .show()
                true
            }
        }

        findPreference<Preference>(Constants.PREF_ABOUT)?.apply {
            setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
        }
    }
}