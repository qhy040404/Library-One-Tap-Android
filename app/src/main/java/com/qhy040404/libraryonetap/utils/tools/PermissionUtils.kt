package com.qhy040404.libraryonetap.utils.tools

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast

object PermissionUtils {
    fun checkPermission(activity: Activity, permission: Array<String>): Boolean {
        var hasPermission = true
        for (s in permission) {
            if (activity.checkSelfPermission(s) == PackageManager.PERMISSION_DENIED) {
                hasPermission = false
                requestPermission(arrayOf(s), activity)
            }
        }
        return hasPermission
    }

    private fun requestPermission(s: Array<String>, activity: Activity) {
        activity.showToast(R.string.permission_prompt)
        ActivityCompat.requestPermissions(activity, s, 100)
    }
}