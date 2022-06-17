package com.qhy040404.libraryonetap.constant

import android.content.pm.PackageManager
import com.google.gson.Gson
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils

object GlobalManager {
    val packageManager: PackageManager by lazy { LibraryOneTapApp.app.packageManager }
    val lazyMgr = ResettableLazyUtils.resettableManager()
    val gson: Gson by lazy { Gson() }
}