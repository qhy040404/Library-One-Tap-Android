package com.qhy040404.libraryonetap.ui.tools

import androidx.annotation.StringRes
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.recyclerview.SimplePageActivity
import com.qhy040404.libraryonetap.utils.web.Requests

abstract class BaseEduActivity : SimplePageActivity() {
  protected var currentVisible = true

  fun showInitFailedAlertDialog(@StringRes title: Int) {
    MaterialAlertDialogBuilder(this)
      .setTitle(title)
      .setMessage(GlobalValues.ssoPrompt)
      .setPositiveButton(R.string.glb_ok) { _, _ ->
        finish()
      }
      .setCancelable(false)
      .create().also {
        if (this.currentVisible) {
          it.show()
        }
      }
  }

  fun initMinor() {
    if (GlobalValues.majorStuId == 0 || GlobalValues.minorStuId == 0) {
      val initUrl = Requests.get(URLManager.EDU_GRADE_INIT_URL, getUrl = true)
      val initData = Requests.get(URLManager.EDU_GRADE_INIT_URL)

      if (initUrl.contains("semester-index")) {
        GlobalValues.majorStuId = initUrl.substringAfter("semester-index/").toInt()
        GlobalValues.minorStuId = -1
      } else {
        val initList = initData.split("onclick=\"myFunction(this)\" value=\"")
        if (initList.size == 3) {
          val aStuId = initList[1].substringBefore("\"").toInt()
          val bStuId = initList[2].substringBefore("\"").toInt()
          GlobalValues.majorStuId = aStuId.coerceAtMost(bStuId)
          GlobalValues.minorStuId = aStuId.coerceAtLeast(bStuId)
        }
      }
    }
  }
}
