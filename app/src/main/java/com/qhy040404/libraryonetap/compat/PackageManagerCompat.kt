package com.qhy040404.libraryonetap.compat

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.utils.OsUtils

@Suppress("DEPRECATION")
object PackageManagerCompat {
    fun getPackageInfo(packageName: String, flags: Int): PackageInfo {
        return if (OsUtils.atLeastT()) {
            GlobalManager.packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            GlobalManager.packageManager.getPackageInfo(packageName, flags)
        }
    }

    fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        return if (OsUtils.atLeastT()) {
            GlobalManager.packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(flags.toLong())
            )
        } else {
            GlobalManager.packageManager.getApplicationInfo(packageName, flags)
        }
    }
}
