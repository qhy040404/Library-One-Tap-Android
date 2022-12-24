package com.qhy040404.libraryonetap.ui.dialog

import android.app.Activity
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.ReserveData
import com.qhy040404.libraryonetap.data.SessionData
import com.qhy040404.libraryonetap.utils.TimeUtils
import com.qhy040404.libraryonetap.utils.Toasty
import com.qhy040404.libraryonetap.utils.encrypt.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.library.ReserveUtils
import com.qhy040404.libraryonetap.utils.library.RoomUtils
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import com.qhy040404.libraryonetap.utils.web.Requests

class ReserveDialog {
    fun showAlertDialog(ctx: Activity) {
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reserve, null)
        val areaSpinner = view.findViewById<Spinner>(R.id.reserve_area)
        val roomSpinner = view.findViewById<Spinner>(R.id.reserve_room)

        var targetRoom = 0
        ArrayAdapter.createFromResource(
            ctx,
            R.array.area_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            areaSpinner.adapter = adapter
        }

        areaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                ArrayAdapter.createFromResource(
                    ctx,
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

        MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.library)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ -> reserveSeat(ctx, targetRoom) }
            .setNegativeButton(R.string.no) { _, _ -> }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun reserveSeat(ctx: Activity, target: Int) {
        if (!TimeUtils.isValidReserveTime()) {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.library)
                .setMessage(R.string.not_in_valid_time)
                .setPositiveButton(R.string.ok, null)
                .setCancelable(true)
                .create()
                .show()
            return
        }

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        var timer = 0
        var loginSuccess = false
        while (!loginSuccess) {
            val ltResponse = Requests.get(URLManager.LIBRARY_SSO_URL)
            val ltData = runCatching {
                ltResponse.substringBetween("LT", "cas", includeDelimiter = true)
            }.getOrDefault(Constants.STRING_NULL)
            val ltExecution = runCatching {
                ltResponse.substringBetween("name=\"execution\" value=\"", "\"")
            }.getOrDefault(Constants.STRING_NULL)

            if (ltData.isNotEmpty()) {
                val rawData = "${GlobalValues.id}${GlobalValues.passwd}$ltData"
                val rsa = DesEncryptUtils.strEnc(rawData, "1", "2", "3")

                Requests.post(
                    URLManager.LIBRARY_SSO_URL,
                    Requests.loginPostData(GlobalValues.id,
                        GlobalValues.passwd,
                        ltData,
                        rsa,
                        ltExecution),
                    GlobalValues.ctSso
                )
            }

            val session = Requests.post(
                URLManager.LIBRARY_SESSION_URL, "",
                GlobalValues.ctSso
            )
            if (SessionData.isSuccess(session)) {
                loginSuccess = true
            } else {
                timer++
                ctx.showToast(R.string.fail_to_login)
                if (timer == 2) {
                    Requests.netLazyMgr.reset()
                    CookieJarImpl.reset()
                }
                if (timer >= 3) {
                    Toasty.cancel()
                    MaterialAlertDialogBuilder(ctx)
                        .setTitle(R.string.library)
                        .setMessage(R.string.fail_to_login_three_times)
                        .setPositiveButton(R.string.ok, null)
                        .setCancelable(true)
                        .create()
                        .show()
                }
            }
        }
        val addCodeOrigin = Requests.post(
            URLManager.LIBRARY_RESERVE_URL,
            ReserveUtils.constructPara(target),
            GlobalValues.ctVCard
        )
        val addCode = ReserveData.getAddCode(addCodeOrigin)
        Requests.post(
            URLManager.LIBRARY_RESERVE_FINAL_URL,
            ReserveUtils.constructParaForFinalReserve(addCode),
            GlobalValues.ctVCard
        )
        MaterialAlertDialogBuilder(ctx)
            .setTitle(R.string.library)
            .setMessage(R.string.reserved)
            .setPositiveButton(R.string.ok) { _, _ -> ctx.recreate() }
            .setCancelable(false)
            .create()
            .show()
    }
}
