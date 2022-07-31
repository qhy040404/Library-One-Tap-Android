package com.qhy040404.libraryonetap.utils

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.core.content.edit
import androidx.core.text.toSpannable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import rikka.material.app.DayNightDelegate
import java.util.*

object AppUtils {
    fun getNightMode(modeString: String): Int {
        return when (modeString) {
            "on" -> DayNightDelegate.MODE_NIGHT_YES
            "off" -> DayNightDelegate.MODE_NIGHT_NO
            else -> DayNightDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    var locale: Locale = Locale.getDefault()
        get() {
            val tag = GlobalValues.locale
            if (tag.isEmpty() || "system" == tag) {
                return Locale.getDefault()
            }
            return Locale.forLanguageTag(tag)
        }
        set(value) {
            field = value
            SPUtils.sp.edit { putString(Constants.PREF_LOCALE, value.toLanguageTag()) }
        }

    fun setTitle(ctx: Context): Spannable {
        return SpannableStringBuilder(ctx.getString(R.string.app_name)).toSpannable()
    }

    fun getThemeID(theme: String): Int {
        return when (theme) {
            "purple" -> R.style.Theme_Purple_NoActionBar
            "library" -> R.style.Theme_Main_NoActionBar
            "blue" -> R.style.Theme_Blue_NoActionBar
            "pink" -> R.style.Theme_Pink_NoActionBar
            "green" -> R.style.Theme_Green_NoActionBar
            "orange" -> R.style.Theme_Orange_NoActionBar
            "red" -> R.style.Theme_Red_NoActionBar
            "simple" -> R.style.Theme_Simple_NoActionBar
            else -> getThemeID(RandomDataUtils.randomTheme)
        }
    }

    fun checkData(id: String, passwd: String): Boolean {
        return id != "Error" && passwd != "Error"
    }

    fun checkDataAndDialog(
        ctx: Context,
        id: String,
        passwd: String,
        titleResId: Int,
        messageResId: Int,
    ): Boolean {
        return if (id == "Error" || passwd == "Error") {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(R.string.ok) { _, _ -> }
                .setCancelable(true)
                .create()
                .show()
            false
        } else {
            true
        }
    }

    fun clearAppData(app: Application) {
        val am = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.clearApplicationUserData()
    }

    fun currentIsNightMode(ctx: Context): Boolean {
        val uiMode = ctx.resources.configuration.uiMode
        return when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }
    }

    fun pass() {
        Log.i("Pass", "Slack off")
    }
}