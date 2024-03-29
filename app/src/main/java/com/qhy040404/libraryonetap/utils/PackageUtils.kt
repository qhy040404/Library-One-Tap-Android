package com.qhy040404.libraryonetap.utils

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.compat.PMCompat

object PackageUtils {
  private val packInfo: PackageInfo =
    PMCompat.getPackageInfo(LibraryOneTapApp.app.packageName, 0)
  private val appInfo: ApplicationInfo = PMCompat.getApplicationInfo(
    LibraryOneTapApp.app.packageName,
    PackageManager.GET_META_DATA
  )

  val versionCode = packInfo.longVersionCode
  val versionName: String = packInfo.versionName

  val buildType = appInfo.metaData.getString("Channel").toString()
}
