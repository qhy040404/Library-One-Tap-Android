package com.qhy040404.libraryonetap.ui.dialog

import android.app.Activity
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.ReserveData
import com.qhy040404.libraryonetap.data.SessionData
import com.qhy040404.libraryonetap.utils.ReserveUtils
import com.qhy040404.libraryonetap.utils.RoomUtils
import com.qhy040404.libraryonetap.utils.des.DesEncryptUtils
import com.qhy040404.libraryonetap.utils.web.Requests

class ReserveDialog {
    fun showAlertDialog(ctx: Activity) {
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reserve, null)
        val areaSpinner = view.findViewById<Spinner>(R.id.spinner3)
        val roomSpinner = view.findViewById<Spinner>(R.id.spinner4)

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
            .setPositiveButton(R.string.ok) { _, _ ->
                reserveSeat(ctx, targetRoom)
                MaterialAlertDialogBuilder(ctx)
                    .setTitle(R.string.library)
                    .setMessage(R.string.reserved)
                    .setPositiveButton(R.string.ok) { _, _ -> ctx.recreate() }
                    .setCancelable(false)
                    .create()
                    .show()
            }
            .setNegativeButton(R.string.no) { _, _ -> }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun reserveSeat(ctx: Activity, target: Int) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build()
        )

        val des = DesEncryptUtils()

        var loginSuccess = false
        while (!loginSuccess) {
            val ltResponse: String = Requests.get(URLManager.LIBRARY_SSO_URL)
            val ltData: String = try {
                "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
            } catch (_: Exception) {
                ""
            }
            val ltExecution: String = try {
                ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
            } catch (_: Exception) {
                ""
            }

            if (ltData != "") {
                val rawData = "${GlobalValues.id}${GlobalValues.passwd}$ltData"
                val rsa: String = des.strEnc(rawData, "1", "2", "3")

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

            val session: String = Requests.post(
                URLManager.LIBRARY_SESSION_URL, "",
                GlobalValues.ctSso
            )
            if (SessionData.isSuccess(session)) {
                loginSuccess = true
                Toast.makeText(ctx, R.string.loaded, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(ctx, R.string.fail_to_login, Toast.LENGTH_SHORT).show()
            }
        }
        val addCodeOrigin =
            Requests.post(
                URLManager.LIBRARY_RESERVE_URL,
                ReserveUtils.constructPara(target),
                GlobalValues.ctVCard
            )
        val addCode = ReserveData.getAddCode(addCodeOrigin)
        Requests.post(
            URLManager.LIBRART_RESERVE_FINAL_URL,
            ReserveUtils.constructParaForFinalReserve(addCode),
            GlobalValues.ctVCard
        )
    }
}