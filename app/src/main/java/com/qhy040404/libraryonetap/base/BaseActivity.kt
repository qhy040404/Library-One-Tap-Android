package com.qhy040404.libraryonetap.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import androidx.viewbinding.ViewBinding
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.AppUtils
import rikka.material.app.MaterialActivity

abstract class BaseActivity<VB : ViewBinding> : MaterialActivity(), IBinding<VB> {
    override lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        if (!GlobalValues.md3) {
            setTheme(AppUtils.getThemeID(GlobalValues.theme))
        }

        super.onCreate(savedInstanceState)
        LibraryOneTapApp.instance?.addActivity(this)

        binding = inflateBinding<VB>(layoutInflater).also {
            setContentView(it.root)
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onDestroy() {
        super.onDestroy()
        LibraryOneTapApp.instance?.removeActivity(this)
    }

    override fun shouldApplyTranslucentSystemBars(): Boolean {
        return true
    }

    override fun computeUserThemeKey(): String? {
        return GlobalValues.darkMode + GlobalValues.theme + GlobalValues.md3
    }

    override fun onApplyTranslucentSystemBars() {
        super.onApplyTranslucentSystemBars()
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.post {
            window.navigationBarColor = Color.TRANSPARENT
            window.isNavigationBarContrastEnforced = false
        }
    }

    override fun onApplyUserThemeResource(theme: Resources.Theme, isDecorView: Boolean) {
        theme.applyStyle(R.style.ThemeOverlay, true)

        if (!GlobalValues.md3) {
            theme.applyStyle(AppUtils.getThemeID(GlobalValues.theme), true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun init()
}
