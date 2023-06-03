package com.qhy040404.libraryonetap.compat

import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.utils.OsUtils

@Suppress("DEPRECATION")
/**
 * Modified Package Manager implementations
 */
object PMCompat {
    private val packageManager by lazy { LibraryOneTapApp.app.packageManager }

    fun getPackageInfo(packageName: String, flags: Int): PackageInfo {
        return if (OsUtils.atLeastT()) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            packageManager.getPackageInfo(packageName, flags)
        }
    }

    fun getApplicationInfo(packageName: String, flags: Int): ApplicationInfo {
        return if (OsUtils.atLeastT()) {
            packageManager.getApplicationInfo(
                packageName,
                PackageManager.ApplicationInfoFlags.of(flags.toLong())
            )
        } else {
            packageManager.getApplicationInfo(packageName, flags)
        }
    }
}
