package com.qhy040404.libraryonetap.base

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.OsUtils
import com.qhy040404.libraryonetap.utils.extensions.CompatExtensions.inflateBinding
import rikka.material.app.MaterialActivity
import java.util.*

@Suppress("DEPRECATION")
abstract class BaseActivity<VB : ViewBinding> : MaterialActivity() {

    protected lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        val config = resources.configuration
        val dm = resources.displayMetrics

        when (GlobalValues.darkMode) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
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

        if (!this::binding.isInitialized) {
            binding = inflateBinding(layoutInflater)
        }

        setContentView(getLayoutId())

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun shouldApplyTranslucentSystemBars(): Boolean {
        return true
    }

    override fun computeUserThemeKey(): String? {
        return GlobalValues.darkMode + GlobalValues.md3
    }

    override fun onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars()
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.post {
            window.navigationBarColor = Color.TRANSPARENT
            if (OsUtils.atLeastQ()) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }

    override fun onApplyUserThemeResource(theme: Resources.Theme, isDecorView: Boolean) {
        theme.applyStyle(R.style.ThemeOverlay, true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun init()

    protected abstract fun getLayoutId(): Int
}