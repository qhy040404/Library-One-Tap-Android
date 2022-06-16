package com.qhy040404.libraryonetap.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.RandomDataUtils
import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils
import java.util.*

@Suppress("DEPRECATION")
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val config = resources.configuration
        val dm = resources.displayMetrics
        val lazyMgr = ResettableLazyUtils.resettableManager()

        when (GlobalValues.darkMode) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "on" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "off" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        when (GlobalValues.theme) {
            "random" -> {
                val randomTheme by ResettableLazyUtils.resettableLazy(lazyMgr) { RandomDataUtils.getNum(4) }
                when (randomTheme) {
                    0-> setTheme(R.style.Theme_Purple)
                    1-> setTheme(R.style.Theme_Blue)
                    2-> setTheme(R.style.Theme_Pink)
                    3-> setTheme(R.style.Theme_Green)
                    4-> setTheme(R.style.Theme_Simple)
                }
            }
            "purple" -> setTheme(R.style.Theme_Purple)
            "blue" -> setTheme(R.style.Theme_Blue)
            "pink" -> setTheme(R.style.Theme_Pink)
            "green" -> setTheme(R.style.Theme_Green)
            "simple" -> setTheme(R.style.Theme_Simple)
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

        setContentView(getLayoutId())

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        init()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return super.onOptionsItemSelected(item)
    }

    protected abstract fun init()

    protected abstract fun getLayoutId(): Int
}