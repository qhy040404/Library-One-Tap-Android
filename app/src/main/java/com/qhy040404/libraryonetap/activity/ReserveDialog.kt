package com.qhy040404.libraryonetap.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.GlobalValues.id
import com.qhy040404.libraryonetap.constant.GlobalValues.passwd
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.utils.ReserveUtils
import com.qhy040404.libraryonetap.utils.RoomUtils
import com.qhy040404.libraryonetap.web.CheckSession
import com.qhy040404.libraryonetap.web.Requests

class ReserveDialog {
    @SuppressLint("InflateParams")
    fun showAlertDialog(ctx: Context) {
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_reserve, null)
        val areaSpinner = view.findViewById<Spinner>(R.id.spinner3)
        val roomSpinner = view.findViewById<Spinner>(R.id.spinner4)

        var targetRoom = 0
        ArrayAdapter.createFromResource(
            ctx,
            R.array.areaArray,
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

        AlertDialog.Builder(ctx)
            .setTitle(R.string.library)
            .setView(view)
            .setPositiveButton(R.string.ok) { _, _ ->
                reserveSeat(ctx, targetRoom)
                AlertDialog.Builder(ctx)
                    .setTitle(R.string.library)
                    .setMessage(R.string.reserved)
                    .setPositiveButton(R.string.ok) {_,_ ->}
                    .setCancelable(false)
                    .create()
                    .show()
            }
            .setNegativeButton(R.string.no) { _, _ -> }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun reserveSeat(ctx: Context, target: Int) {
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

        val requests = Requests()
        val des = desEncrypt()
        val checkSession = CheckSession()

        var loginSuccess = false
        while (!loginSuccess) {
            val ltResponse: String = requests.get(URLManager.LIBRARY_SSO_URL)
            val ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

            val rawData = "$id$passwd$ltData"
            val rsa: String = des.strEnc(rawData, "1", "2", "3")

            requests.post(
                URLManager.LIBRARY_SSO_URL,
                requests.loginPostData(id, passwd, ltData, rsa),
                GlobalValues.ctSso
            )

            val session: String = requests.post(
                URLManager.LIBRARY_SESSION_URL, "",
                GlobalValues.ctSso
            )
            if (checkSession.isSuccess(session)) {
                loginSuccess = true
                Toast.makeText(ctx, R.string.loaded, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(ctx, R.string.logFail, Toast.LENGTH_LONG).show()
            }
        }
        requests.post(
            URLManager.LIBRARY_RESERVE_URL,
            ReserveUtils.constructPara(target),
            GlobalValues.ctVCard
        )
    }
}