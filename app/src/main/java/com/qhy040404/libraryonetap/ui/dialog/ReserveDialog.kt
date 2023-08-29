package com.qhy040404.libraryonetap.ui.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.ReserveDTO
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.library.ReserveUtils
import com.qhy040404.libraryonetap.utils.library.RoomUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReserveDialog {
  fun showAlertDialog(activity: Activity) {
    val view = LayoutInflater.from(activity).inflate(R.layout.dialog_reserve, null)
    val areaSpinner = view.findViewById<AppCompatSpinner>(R.id.reserve_area)
    val roomSpinner = view.findViewById<AppCompatSpinner>(R.id.reserve_room)

    var targetRoom = 0
    ArrayAdapter.createFromResource(
      activity,
      R.array.area_array,
      android.R.layout.simple_spinner_item
    ).also { adapter ->
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
      areaSpinner.adapter = adapter
    }

    areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        ArrayAdapter.createFromResource(
          activity,
          when (areaSpinner.selectedItem.toString()) {
            "伯川", "Bochuan" -> R.array.BCArray
            else -> R.array.LXArray
          },
          android.R.layout.simple_spinner_item
        ).also { adapter ->
          adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
          roomSpinner.adapter = adapter
        }
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {}
    }

    roomSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        targetRoom = RoomUtils.getRoomCode(
          areaSpinner.selectedItem.toString(),
          roomSpinner.selectedItem.toString()
        )
      }

      override fun onNothingSelected(p0: AdapterView<*>?) {}
    }

    MaterialAlertDialogBuilder(activity)
      .setTitle(R.string.library)
      .setView(view)
      .setPositiveButton(R.string.glb_ok) { _, _ -> reserveSeat(activity, targetRoom) }
      .setNegativeButton(R.string.glb_no) { _, _ -> }
      .setCancelable(true)
      .create()
      .show()
  }

  private fun reserveSeat(activity: Activity, target: Int) {
    if (!TimeUtils.isValidReserveTime()) {
      MaterialAlertDialogBuilder(activity)
        .setTitle(R.string.library)
        .setMessage(R.string.df_not_in_valid_time)
        .setPositiveButton(R.string.glb_ok, null)
        .setCancelable(true)
        .create()
        .show()
      return
    }

    if (!Requests.initLib()) {
      MaterialAlertDialogBuilder(activity)
        .setTitle(R.string.library)
        .setMessage(GlobalValues.netPrompt)
        .setPositiveButton(R.string.glb_ok, null)
        .setCancelable(true)
        .create()
        .show()
    } else {
      (activity as LifecycleOwner).lifecycleScope.launch(Dispatchers.IO) {
        val addCode = getAddCode(target)
        Requests.post(
          URLManager.LIBRARY_RESERVE_FINAL_URL,
          ReserveUtils.constructParaForFinalReserve(addCode),
          GlobalValues.ctVCard
        )
        activity.runOnUiThread {
          MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.library)
            .setMessage(R.string.tlp_reserved)
            .setPositiveButton(R.string.glb_ok) { _, _ -> activity.recreate() }
            .setCancelable(false)
            .create()
            .show()
        }
      }
    }
  }

  private fun getAddCode(target: Int, timer: Int = 0): String {
    return Requests.post(
      URLManager.LIBRARY_RESERVE_URL,
      ReserveUtils.constructPara(target),
      GlobalValues.ctVCard
    ).decode<ReserveDTO>()?.data?.addCode ?: run {
      if (timer >= 5) return ""
      getAddCode(target, timer + 1)
    }
  }
}
