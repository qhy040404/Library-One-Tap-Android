package com.qhy040404.libraryonetap.constant

import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.PackageUtils
import com.qhy040404.libraryonetap.utils.SPDelegates
import com.qhy040404.libraryonetap.utils.SPUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import okhttp3.MediaType

object GlobalValues {
    // SharedPreferences
    const val SP_NAME = "${BuildConfig.APPLICATION_ID}_preferences"

    // App
    val version =
        LibraryOneTapApp.app.getString(R.string.app_name) + " ${PackageUtils.buildType} v${PackageUtils.versionName} (${PackageUtils.versionCode})"

    // Preferences
    var name: String by SPDelegates(Constants.PREF_NAME, Constants.GLOBAL_ERROR)
    var id: String by SPDelegates(Constants.PREF_ID, Constants.GLOBAL_ERROR)
    var passwd: String by SPDelegates(Constants.PREF_PASSWD, Constants.GLOBAL_ERROR)

    var darkMode: String by SPDelegates(Constants.PREF_DARK, Constants.DEFAULT_DARK)
    var theme: String by SPDelegates(Constants.PREF_THEME, Constants.DEFAULT_THEME)
    var md3: Boolean by SPDelegates(Constants.PREF_MD3, false)
    var locale: String by SPDelegates(Constants.PREF_LOCALE, Constants.DEFAULT_LOCALE)

    var initialized: Boolean by SPDelegates(Constants.PREF_INIT, false)

    // MediaType
    val ctJson: MediaType = Requests.strToMT(Constants.CONTENT_TYPE_JSON)
    val ctSso: MediaType = Requests.strToMT(Constants.CONTENT_TYPE_SSO)
    val ctVCard: MediaType = Requests.strToMT(Constants.CONTENT_TYPE_VCARD)

    // Grades
    var minorVisible: Boolean = false
    var minorDetected: Boolean = false

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