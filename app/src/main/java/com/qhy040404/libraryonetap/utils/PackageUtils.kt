package com.qhy040404.libraryonetap.utils

import android.content.pm.PackageInfo
import com.qhy040404.libraryonetap.LibraryOneTapApp

object PackageUtils {
    private val packInfo: PackageInfo =
        LibraryOneTapApp.app.packageManager.getPackageInfo(LibraryOneTapApp.app.packageName, 0)
    val versionCode = packInfo.longVersionCode
    val versionName: String = packInfo.versionName
}