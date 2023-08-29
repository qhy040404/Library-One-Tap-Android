package com.qhy040404.libraryonetap.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import com.qhy040404.libraryonetap.ui.fragment.fullscreen.FullScreenDialogFragment
import com.qhy040404.libraryonetap.utils.extensions.getString

object PermissionUtils {
  fun checkPermission(
    activity: Activity,
    permission: String,
    childFragmentMgr: FragmentManager,
    @StringRes strId: Int
  ): Boolean {
    return if (activity.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
      FullScreenDialogFragment(strId.getString()).apply {
        show(childFragmentMgr, null)
      }
      activity.requestPermissions(arrayOf(permission), 100)
      false
    } else {
      true
    }
  }
}
