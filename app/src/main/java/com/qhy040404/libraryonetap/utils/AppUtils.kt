package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
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
            if (tag.isEmpty() || "SYSTEM" == tag || "system" == tag) {
                return Locale.getDefault()
            }
            return Locale.forLanguageTag(tag)
        }
        set(value) {
            field = value
            SPUtils.sp.edit { putString(Constants.PREF_LOCALE, value.toLanguageTag()) }
        }

    fun setTitle(ctx: Context): Spannable {
        val sb = SpannableStringBuilder(ctx.getString(R.string.app_name))
        return sb.toSpannable()
    }

    fun getThemeID(theme: String): Int {
        return when (theme) {
            "purple" -> R.style.Theme_Purple_NoActionBar
            "blue" -> R.style.Theme_Blue_NoActionBar
            "pink" -> R.style.Theme_Pink_NoActionBar
            "green" -> R.style.Theme_Green_NoActionBar
            "orange" -> R.style.Theme_Orange_NoActionBar
            "red" -> R.style.Theme_Red_NoActionBar
            "simple" -> R.style.Theme_Simple_NoActionBar
            else -> getThemeID(RandomDataUtils.randomTheme)
        }
    }

    /**
     * Check if has id and passwd
     * @param id String ( First original data )
     * @param passwd String ( Second original data )
     * @param targetOne String ( First target data )
     * @param targetTwo String ( Second target data )
     * @return Boolean
     */

    fun checkData(id: String, passwd: String, targetOne: String, targetTwo: String): Boolean {
        return id != targetOne && passwd != targetTwo
    }

    fun checkData(id: String, passwd: String): Boolean {
        return id != "Error" && passwd != "Error"
    }

    /**
     * Check id and passwd and show a dialog
     * @param ctx Context ( Dialog required context )
     * @param id String ( First original data )
     * @param passwd String ( Second original data )
     * @param targetOne String ( First target data )
     * @param targetTwo String ( Second target data )
     * @param titleResId Int ( Dialog title resource ID )
     * @param messageResId Int ( Dialog message resource ID )
     * @return Boolean
     */

    fun checkDataAndDialog(
        ctx: Context,
        id: String,
        passwd: String,
        targetOne: String,
        targetTwo: String,
        titleResId: Int,
        messageResId: Int,
    ): Boolean {
        return if (id == targetOne || passwd == targetTwo) {
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
}