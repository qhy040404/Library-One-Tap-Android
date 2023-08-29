package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants

object AppUtils {
  fun getNightMode(modeString: String) = when (modeString) {
    "on" -> AppCompatDelegate.MODE_NIGHT_YES
    "off" -> AppCompatDelegate.MODE_NIGHT_NO
    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
  }

  fun getThemeID(theme: String): Int {
    return when (theme) {
      "purple" -> R.style.Theme_Purple_NoActionBar
      "library" -> R.style.Theme_Main_NoActionBar
      "blue" -> R.style.Theme_Blue_NoActionBar
      "pink" -> R.style.Theme_Pink_NoActionBar
      "green" -> R.style.Theme_Green_NoActionBar
      "orange" -> R.style.Theme_Orange_NoActionBar
      "red" -> R.style.Theme_Red_NoActionBar
      "simple" -> R.style.Theme_Simple_NoActionBar
      else -> getThemeID(RandomDataUtils.randomTheme)
    }
  }

  fun checkData(
    id: String,
    passwd: String,
    context: Context? = null,
    @StringRes titleId: Int? = null,
    @StringRes messageId: Int? = null
  ): Boolean {
    (id != "Error" && passwd != "Error" && id.isNotEmpty() && passwd.isNotEmpty()).let {
      if (!it && context != null && titleId != null && messageId != null) {
        MaterialAlertDialogBuilder(context)
          .setTitle(titleId)
          .setMessage(messageId)
          .setPositiveButton(R.string.glb_ok, null)
          .setCancelable(true)
          .create()
          .show()
      }
      return it
    }
  }

  fun currentIsNightMode(context: Context): Boolean {
    val uiMode = context.resources.configuration.uiMode
    return when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
      Configuration.UI_MODE_NIGHT_YES -> true
      else -> false
    }
  }

  fun hasNetwork(): Boolean {
    return NetworkStateUtils.checkNetworkType() != Constants.GLOBAL_ERROR
  }
}
