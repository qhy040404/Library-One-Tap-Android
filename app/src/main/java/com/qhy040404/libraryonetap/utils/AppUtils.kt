package com.qhy040404.libraryonetap.utils

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.text.toSpannable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.utils.tools.NetworkStateUtils
import rikka.material.app.DayNightDelegate

@Suppress("MemberVisibilityCanBePrivate")
object AppUtils {
    private val ctx = LibraryOneTapApp.app

    fun getNightMode(modeString: String) = when (modeString) {
        "on" -> DayNightDelegate.MODE_NIGHT_YES
        "off" -> DayNightDelegate.MODE_NIGHT_NO
        else -> DayNightDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }

    fun setTitle(ctx: Context) =
        SpannableStringBuilder(ctx.getString(R.string.app_name)).toSpannable()

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
                .setPositiveButton(R.string.ok, null)
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

    fun pass() = Log.i("Pass", "Slack off")

    fun getResString(@StringRes resId: Int): String {
        val conf = ctx.resources.configuration.also {
            it.setLocale(GlobalValues.locale)
        }
        return ctx.createConfigurationContext(conf).getString(resId)
    }

    @Suppress("unused")
    fun isError(a: String): Boolean = isError(a, "")

    fun isError(a: String, b: String): Boolean = isError(a, b, "")

    fun isError(a: String, b: String, c: String): Boolean = isError(a, b, c, "")

    fun isError(a: String, b: String, c: String, d: String): Boolean = isError(a, b, c, d, "")

    fun isError(a: String, b: String, c: String, d: String, e: String): Boolean {
        return a == Constants.GLOBAL_ERROR || b == Constants.GLOBAL_ERROR || c == Constants.GLOBAL_ERROR || d == Constants.GLOBAL_ERROR || e == Constants.GLOBAL_ERROR
    }

    fun hasNetwork(): Boolean {
        return NetworkStateUtils.checkNetworkTypeStr(ctx) != Constants.GLOBAL_ERROR
    }
}
