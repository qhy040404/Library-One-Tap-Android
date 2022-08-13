package com.qhy040404.libraryonetap.utils.tools

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.ui.fragment.fullscreen.FullScreenDialogFragment

object PermissionUtils {
    fun checkPermission(
        activity: Activity,
        permission: Array<String>,
        childFragmentMgr: FragmentManager,
    ): Boolean {
        var hasPermission = true
        for (s in permission) {
            if (activity.checkSelfPermission(s) == PackageManager.PERMISSION_DENIED) {
                hasPermission = false
                requestPermission(arrayOf(s),
                    activity,
                    childFragmentMgr,
                    GlobalManager.bathPermissionFullScrFragment)
            }
        }
        return hasPermission
    }

    private fun requestPermission(
        s: Array<String>,
        activity: Activity,
        fragmentMgr: FragmentManager,
        fsFragment: FullScreenDialogFragment,
    ) {
        fsFragment.show(fragmentMgr, null)
        ActivityCompat.requestPermissions(activity, s, 100)
    }
}