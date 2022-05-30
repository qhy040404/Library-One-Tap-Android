package com.qhy040404.libraryonetap.fragment.tools

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.qhy040404.libraryonetap.R

class ToolsInitNewFragment : PreferenceFragmentCompat(){
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.tools_list, rootKey)

        /*
        findPreference<Preference>(STRING)?.apply {
            setOnPreferenceClickListener {
                TODO
            }
        }
        */
    }
}