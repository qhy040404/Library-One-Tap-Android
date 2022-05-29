package com.qhy040404.libraryonetap.constant

import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants.CONTENT_TYPE_JSON
import com.qhy040404.libraryonetap.constant.Constants.CONTENT_TYPE_SSO
import com.qhy040404.libraryonetap.constant.Constants.CONTENT_TYPE_VCARD
import com.qhy040404.libraryonetap.utils.PackageUtils.versionCode
import com.qhy040404.libraryonetap.utils.PackageUtils.versionName
import com.qhy040404.libraryonetap.utils.SPDelegates
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import okhttp3.MediaType
import java.util.*

const val SP_NAME = "${BuildConfig.APPLICATION_ID}_preferences"

object GlobalValues {
    private fun getAndReturnLocale(): Locale {
        return when (SPUtils.sp.getString(Constants.PREF_LOCALE, Constants.DEFAULT_LOCALE)) {
            "zh" -> Locale.SIMPLIFIED_CHINESE
            "en" -> Locale.ENGLISH
            else -> Locale.getDefault()
        }
    }

    // App
    val version =
        LibraryOneTapApp.app.getString(R.string.app_name) + " " + versionName + "($versionCode)"

    // Preferences
    var name: String by SPDelegates(Constants.PREF_NAME, Constants.GLOBAL_ERROR)
    var id: String by SPDelegates(Constants.PREF_ID, Constants.GLOBAL_ERROR)
    var passwd: String by SPDelegates(Constants.PREF_PASSWD, Constants.GLOBAL_ERROR)

    var darkMode: String by SPDelegates(Constants.PREF_DARK, Constants.DEFAULT_DARK)

    var theme: String by SPDelegates(Constants.PREF_THEME, Constants.DEFAULT_THEME)

    var locale: Locale = getAndReturnLocale()

    // MediaType
    val ctJson: MediaType = Requests().strToMT(CONTENT_TYPE_JSON)
    val ctSso: MediaType = Requests().strToMT(CONTENT_TYPE_SSO)
    val ctVCard: MediaType = Requests().strToMT(CONTENT_TYPE_VCARD)
}