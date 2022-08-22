package com.qhy040404.libraryonetap.constant

import androidx.core.content.edit
import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.PackageUtils
import com.qhy040404.libraryonetap.utils.SPDelegates
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.lazy.resettableLazy
import okhttp3.MediaType.Companion.toMediaType
import java.util.Locale

object GlobalValues {
    // SharedPreferences
    const val SP_NAME = "${BuildConfig.APPLICATION_ID}_preferences"

    // Preferences
    var name: String by SPDelegates(Constants.PREF_NAME, Constants.GLOBAL_ERROR)
    var id: String by SPDelegates(Constants.PREF_ID, Constants.GLOBAL_ERROR)
    var passwd: String by SPDelegates(Constants.PREF_PASSWD, Constants.GLOBAL_ERROR)

    var darkMode: String by SPDelegates(Constants.PREF_DARK, Constants.DEFAULT_DARK)
    var theme: String by SPDelegates(Constants.PREF_THEME, Constants.DEFAULT_THEME)
    var md3: Boolean by SPDelegates(Constants.PREF_MD3, true)
    var locale: Locale = Locale.getDefault()
        get() {
            val tag = SPUtils.sp.getString(Constants.PREF_LOCALE, null)
            if (tag.isNullOrEmpty() || "system" == tag) {
                return Locale.getDefault()
            }
            return Locale.forLanguageTag(tag)
        }
        set(value) {
            field = value
            SPUtils.sp.edit { putString(Constants.PREF_LOCALE, value.toLanguageTag()) }
        }

    var initialized: Boolean by SPDelegates(Constants.PREF_INIT, false)

    // Settings
    var themeInit = false
    var isMD3Changed = false

    // MediaType
    val ctJson = Constants.CONTENT_TYPE_JSON.toMediaType()
    val ctSso = Constants.CONTENT_TYPE_SSO.toMediaType()
    val ctVCard = Constants.CONTENT_TYPE_VCARD.toMediaType()

    // App
    val version by resettableLazy(GlobalManager.lazyMgr) {
        AppUtils.getResString(R.string.app_name) + " ${PackageUtils.buildType} v${PackageUtils.versionName} (${PackageUtils.versionCode})"
    }

    // Grades
    var minorVisible = false

    // Net
    var netError = false

    fun initBasic() {
        SPUtils.spLazyMgr.reset()
        if (name != SPUtils.getValue(Constants.PREF_NAME,
                Constants.GLOBAL_ERROR) && name != Constants.GLOBAL_ERROR
        ) {
            name = SPUtils.getValue(Constants.PREF_NAME, Constants.GLOBAL_ERROR)
        }
        if (id != SPUtils.getValue(Constants.PREF_ID,
                Constants.GLOBAL_ERROR) && id != Constants.GLOBAL_ERROR
        ) {
            id = SPUtils.getValue(Constants.PREF_ID, Constants.GLOBAL_ERROR)
        }
        if (passwd != SPUtils.getValue(Constants.PREF_PASSWD,
                Constants.GLOBAL_ERROR) && passwd != Constants.GLOBAL_ERROR
        ) {
            passwd = SPUtils.getValue(Constants.PREF_PASSWD, Constants.GLOBAL_ERROR)
        }
    }
}
