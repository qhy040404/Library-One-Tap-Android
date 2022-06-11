package com.qhy040404.libraryonetap.utils.tools

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.qhy040404.libraryonetap.R

class PermissionUtils {
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

    companion object {
        private fun requestPermission(s: Array<String>, activity: Activity) {
            Toast.makeText(activity, R.string.promptPermission, Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(activity, s, 100)
        }
    }
}