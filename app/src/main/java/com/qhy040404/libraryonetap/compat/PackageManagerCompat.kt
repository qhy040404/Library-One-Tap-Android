package com.qhy040404.libraryonetap.compat

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.constant.AppManager
import com.qhy040404.libraryonetap.utils.OsUtils

@Suppress("DEPRECATION")
object PackageManagerCompat {
    fun getPackageInfo(packageName: String, flags: Int) :PackageInfo{
        return if (OsUtils.atLeastT()) {
            AppManager.packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            AppManager.packageManager.getPackageInfo(packageName,flags)
        }
    }

    fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        return if (OsUtils.atLeastT()) {
            AppManager.packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(flags.toLong())
            )
        } else {
           AppManager.packageManager.getApplicationInfo(packageName, flags)
        }
    }
}