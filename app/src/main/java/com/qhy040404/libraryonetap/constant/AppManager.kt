package com.qhy040404.libraryonetap.constant

import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.LibraryOneTapApp

object AppManager {
    val packageManager: PackageManager by lazy { LibraryOneTapApp.app.packageManager }
}