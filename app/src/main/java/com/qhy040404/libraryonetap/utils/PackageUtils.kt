package com.qhy040404.libraryonetap.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.LibraryOneTapApp


object PackageUtils {
    private val packInfo: PackageInfo =
        LibraryOneTapApp.app.packageManager.getPackageInfo(LibraryOneTapApp.app.packageName, 0)
    private val appInfo: ApplicationInfo = LibraryOneTapApp.app.packageManager.getApplicationInfo(
        LibraryOneTapApp.app.packageName,
        PackageManager.GET_META_DATA
    )

    val versionCode = packInfo.longVersionCode
    val versionName: String = packInfo.versionName

    val buildType: String = appInfo.metaData.getString("channel").toString()
}