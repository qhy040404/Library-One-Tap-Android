package com.qhy040404.libraryonetap.ui.fragment.settings

import android.app.ActivityManager
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.text.parseAsHtml
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.recyclerview.widget.RecyclerView
import com.absinthe.libraries.utils.extensions.addPaddingTop
import com.absinthe.libraries.utils.utils.UiUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.ui.about.AboutActivity
import com.qhy040404.libraryonetap.ui.interfaces.IAppBarContainer
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.CacheUtils
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.UpdateUtils
import com.qhy040404.libraryonetap.utils.extensions.getDimen
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.isDuplicateGV
import com.qhy040404.libraryonetap.utils.extensions.showToast
import com.qhy040404.libraryonetap.utils.extensions.start
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests
import com.qhy040404.libraryonetap.view.PasswordPreference
import jonathanfinerty.once.Once
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.material.app.LocaleDelegate
import rikka.preference.SimpleMenuPreference
import rikka.recyclerview.fixEdgeEffect
import rikka.widget.borderview.BorderRecyclerView
import rikka.widget.borderview.BorderView
import rikka.widget.borderview.BorderViewDelegate
import java.io.File
import java.util.Locale
import kotlin.system.exitProcess

class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var borderViewDelegate: BorderViewDelegate
    private lateinit var prefRecyclerView: RecyclerView

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        if (GlobalValues.themeInit && GlobalValues.isMD3Changed) {
            if (GlobalValues.md3) {
                setCustomThemeVisibility(false)
            } else {
                setCustomThemeVisibility(true)
            }
            GlobalValues.isMD3Changed = false
        } else {
            findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.isVisible =
                !GlobalValues.md3
            GlobalValues.themeInit = true
        }

        findPreference<EditTextPreference>(Constants.PREF_NAME)?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_TEXT
                text?.let { editText.setSelection(it.length) }
            }
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isDuplicateGV(GlobalValues.name)) {
                    return@setOnPreferenceChangeListener true
                }
                GlobalValues.name = newValue.toString()
                Requests.libInitialized = false
                Requests.eduInitialized = false
                Requests.netLazyMgr.reset()
                CookieJarImpl.reset()
                activity?.recreate()
                true
            }
        }

        findPreference<EditTextPreference>(Constants.PREF_ID)?.apply {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
                text?.let { editText.setSelection(it.length) }
            }
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isDuplicateGV(GlobalValues.id)) {
                    return@setOnPreferenceChangeListener true
                }
                GlobalValues.id = newValue.toString()
                Requests.libInitialized = false
                Requests.eduInitialized = false
                Requests.netLazyMgr.reset()
                CookieJarImpl.reset()
                activity?.recreate()
                true
            }
        }

        findPreference<PasswordPreference>(Constants.PREF_PASSWD)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isDuplicateGV(GlobalValues.passwd)) {
                    return@setOnPreferenceChangeListener true
                }
                GlobalValues.passwd = newValue.toString()
                Requests.libInitialized = false
                Requests.eduInitialized = false
                Requests.netLazyMgr.reset()
                CookieJarImpl.reset()
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_GP)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isDuplicateGV(GlobalValues.gpOption)) {
                    return@setOnPreferenceChangeListener true
                }
                GlobalValues.gpOption = newValue.toString()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_DARK)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isDuplicateGV(GlobalValues.darkMode)) {
                    return@setOnPreferenceChangeListener true
                }
                GlobalValues.darkMode = newValue.toString()
                AppCompatDelegate.setDefaultNightMode(AppUtils.getNightMode(newValue.toString()))
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue.toString().isDuplicateGV(GlobalValues.theme, true)) {
                    return@setOnPreferenceChangeListener true
                }
                GlobalValues.theme = newValue.toString()
                GlobalManager.lazyMgr.reset()
                activity?.recreate()
                true
            }
        }

        findPreference<SwitchPreference>(Constants.PREF_MD3)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                GlobalValues.md3 = newValue as Boolean
                GlobalValues.isMD3Changed = true
                activity?.recreate()
                true
            }
        }

        findPreference<SimpleMenuPreference>(Constants.PREF_LOCALE)?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue is String) {
                    val locale: Locale = if ("system" == newValue) {
                        LocaleDelegate.systemLocale
                    } else {
                        Locale.forLanguageTag(newValue)
                    }
                    LocaleDelegate.defaultLocale = locale
                    GlobalManager.lazyMgr.reset()
                    activity?.recreate()
                }
                true
            }
        }

        findPreference<Preference>(Constants.PREF_RESET)?.apply {
            setOnPreferenceClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage(R.string.stp_data_reset_confirm)
                    .setTitle(R.string.settings_title)
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        SPUtils.resetAll()
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.stp_data_reset_completed)
                            .setTitle(R.string.settings_title)
                            .setPositiveButton(R.string.glb_ok) { _, _ ->
                                LibraryOneTapApp.instance?.exit()
                                Once.clearAll()
                                LibraryOneTapApp.app.getSystemService<ActivityManager>()
                                    ?.clearApplicationUserData()
                                exitProcess(0)
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                    }
                    .setNegativeButton(R.string.glb_no) { _, _ -> }
                    .create()
                    .show()
                true
            }
        }

        findPreference<Preference>(Constants.PREF_CACHE)?.apply {
            summary = CacheUtils.getCacheSize()
            setOnPreferenceClickListener {
                CacheUtils.trimCaches()
                requireContext().showToast(R.string.stp_cache_cleared)
                summary = CacheUtils.getCacheSize()
                true
            }
        }

        findPreference<Preference>(Constants.PREF_UPDATE)?.apply {
            if (GlobalValues.newVersion != null) {
                summary =
                    R.string.upd_available.getString() + GlobalValues.newVersion
            }
            setOnPreferenceClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    UpdateUtils.checkUpdate(requireContext(), true)
                }
                true
            }
        }

        findPreference<Preference>(Constants.PREF_CHANGELOG)?.apply {
            val file = File(requireContext().dataDir, Constants.CHANGELOG_ACTIVE)
            if (!file.exists()) {
                isVisible = false
            }
            setOnPreferenceClickListener {
                val body = file.readText()
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.upd_latest_changelog)
                    .setMessage(body.parseAsHtml())
                    .setPositiveButton(R.string.glb_ok, null)
                    .setCancelable(true)
                    .create().show()
                true
            }
        }

        findPreference<Preference>(Constants.PREF_ISSUE)?.apply {
            setOnPreferenceClickListener {
                URLManager.GITHUB_ISSUE_URL.toUri().start(requireContext())
                true
            }
        }

        findPreference<Preference>(Constants.PREF_ABOUT)?.apply {
            summary = GlobalValues.version
            setOnPreferenceClickListener {
                startActivity(Intent(requireContext(), AboutActivity::class.java))
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        scheduleAppbarRaisingStatus(
            !getBorderViewDelegate().isShowingTopBorder
        )
        (activity as? IAppBarContainer)?.setLiftOnScrollTargetView(prefRecyclerView)
        findPreference<Preference>(Constants.PREF_CACHE)?.summary = CacheUtils.getCacheSize()
        if (GlobalValues.newVersion != null) {
            findPreference<Preference>(Constants.PREF_UPDATE)?.summary =
                R.string.upd_available.getString() + GlobalValues.newVersion
        }
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
            lp.rightMargin = rikka.material.R.dimen.rd_activity_horizontal_margin.getDimen().toInt()
            lp.leftMargin = lp.rightMargin
        }

        borderViewDelegate = recyclerView.borderViewDelegate
        borderViewDelegate.borderVisibilityChangedListener =
            BorderView.OnBorderVisibilityChangedListener { top: Boolean, _: Boolean, _: Boolean, _: Boolean ->
                scheduleAppbarRaisingStatus(
                    !top
                )
            }

        prefRecyclerView = recyclerView
        return recyclerView
    }

    private fun scheduleAppbarRaisingStatus(isLifted: Boolean) {
        (activity as? IAppBarContainer)?.scheduleAppbarLiftingStatus(isLifted)
    }

    private fun getBorderViewDelegate(): BorderViewDelegate = borderViewDelegate

    private fun setCustomThemeVisibility(visible: Boolean) {
        findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.isVisible = !visible
        lifecycleScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                findPreference<SimpleMenuPreference>(Constants.PREF_THEME)?.isVisible = visible
            }
        }
    }
}
