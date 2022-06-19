package com.qhy040404.libraryonetap.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.ui.AboutActivity
import com.qhy040404.libraryonetap.utils.SPUtils
import rikka.preference.SimpleMenuPreference
import kotlin.system.exitProcess

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
                GlobalManager.lazyMgr.reset()
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

        findPreference<Preference>(Constants.PREF_RESET)?.apply {
            setOnPreferenceClickListener {
                AlertDialog.Builder(requireContext())
                    .setMessage(R.string.dataResetConfirm)
                    .setTitle(R.string.title_activity_settings)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        SPUtils.resetAll()
                        AlertDialog.Builder(requireContext())
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