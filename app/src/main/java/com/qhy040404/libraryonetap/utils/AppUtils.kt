package com.qhy040404.libraryonetap.utils

import androidx.core.content.edit
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
            if (tag.isEmpty() || "SYSTEM" == tag) {
                return Locale.getDefault()
            }
            return Locale.forLanguageTag(tag)
        }
        set(value) {
            field = value
            SPUtils.sp.edit { putString(Constants.PREF_LOCALE, value.toLanguageTag()) }
        }
}