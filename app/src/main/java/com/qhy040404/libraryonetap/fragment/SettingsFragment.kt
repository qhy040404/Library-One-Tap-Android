package com.qhy040404.libraryonetap.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.libraries.utils.extensions.addPaddingTop
import com.absinthe.libraries.utils.utils.UiUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.ui.about.AboutActivity
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import rikka.material.app.DayNightDelegate
import rikka.material.app.LocaleDelegate
import rikka.material.preference.MaterialSwitchPreference
import rikka.preference.SimpleMenuPreference
import rikka.recyclerview.fixEdgeEffect
import rikka.widget.borderview.BorderRecyclerView
import rikka.widget.borderview.BorderView
import rikka.widget.borderview.BorderViewDelegate
import java.util.*

class SettingsFragment : PreferenceFragmentCompat(), IListController {

    private lateinit var borderViewDelegate: BorderViewDelegate
    private lateinit var prefRecyclerView: RecyclerView

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.isVisible = !GlobalValues.md3

        findPreference<SimpleMenuPreference>(Constants.PREF_DARK)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                GlobalValues.darkMode = newValue.toString()
                DayNightDelegate.setDefaultNightMode(AppUtils.getNightMode(newValue.toString()))
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                GlobalValues.theme = newValue.toString()
                GlobalManager.lazyMgr.reset()
                activity?.recreate()
                true
            }
        }

        findPreference<MaterialSwitchPreference>(Constants.PREF_MD3)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                GlobalValues.md3 = newValue as Boolean
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
                                AppUtils.clearAppData(LibraryOneTapApp.app)
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

    override fun onResume() {
        super.onResume()
        scheduleAppbarRaisingStatus(
            !getBorderViewDelegate().isShowingTopBorder,
            "SettingsFragment onResume"
        )
        (activity as? IAppBarContainer)?.setLiftOnScrollTargetView(prefRecyclerView)
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?,
    ): RecyclerView {
        val recyclerView =
            super.onCreateRecyclerView(inflater, parent, savedInstanceState) as BorderRecyclerView
        recyclerView.id = android.R.id.list
        recyclerView.fixEdgeEffect()
        recyclerView.addPaddingTop(UiUtils.getStatusBarHeight())
        recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        recyclerView.isVerticalScrollBarEnabled = false

        val lp = recyclerView.layoutParams
        if (lp is FrameLayout.LayoutParams) {
            lp.rightMargin =
                recyclerView.context.resources.getDimension(rikka.material.R.dimen.rd_activity_horizontal_margin)
                    .toInt()
            lp.leftMargin = lp.rightMargin
        }

        borderViewDelegate = recyclerView.borderViewDelegate
        borderViewDelegate.borderVisibilityChangedListener =
            BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                scheduleAppbarRaisingStatus(
                    !top,
                    "SettingsFragment OnBorderVisibilityChangedListener: top=$top"
                )
            }

        prefRecyclerView = recyclerView
        return recyclerView
    }

    private fun scheduleAppbarRaisingStatus(isLifted: Boolean, from: String) {
        (activity as? IAppBarContainer)?.scheduleAppbarLiftingStatus(isLifted, from)
    }

    override fun onReturnTop() {}

    override fun getBorderViewDelegate(): BorderViewDelegate = borderViewDelegate
    override fun isAllowRefreshing(): Boolean = true
    override fun getSuitableLayoutManager(): RecyclerView.LayoutManager? = null
}