package com.qhy040404.libraryonetap.constant

import android.content.pm.PackageManager
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.ui.fragment.fullscreen.FullScreenDialogFragment
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.lazy.ResettableLazyUtils
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object GlobalManager {
    val packageManager: PackageManager by lazy { LibraryOneTapApp.app.packageManager }
    val lazyMgr = ResettableLazyUtils.resettableManager()
    val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    val permissionFullScrFragment =
        FullScreenDialogFragment(AppUtils.getResString(R.string.permission_prompt))
}