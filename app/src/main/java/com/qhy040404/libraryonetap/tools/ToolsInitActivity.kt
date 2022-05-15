package com.qhy040404.libraryonetap.tools

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.data.ElectricData
import com.qhy040404.libraryonetap.data.NetData
import com.qhy040404.libraryonetap.data.getPortalData

class ToolsInitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools_init)
    }

    fun buttonBath(view: View) {
        AlertDialog.Builder(this)
            .setMessage(R.string.networkLimit)
            .setTitle(R.string.bath_title)
            .setPositiveButton(R.string.ok) { _, _ ->
                val intent = Intent(this, BathReserveActivity::class.java)
                startActivity(intent)
            }
            .setCancelable(false)
            .create()
            .show()
    }

    fun buttonNet(view: View) {
        val makeText = Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT)
        makeText.show()
        Thread(getNet()).start()
    }

    fun buttonElectric(view: View) {
        val makeText = Toast.makeText(this, R.string.loading, Toast.LENGTH_SHORT)
        makeText.show()
        Thread(getElectric()).start()
    }

    fun buttonVCard(view: View) {
        val intent = Intent(this, VCardActivity::class.java)
        startActivity(intent)
    }

    private inner class getNet : Runnable {
        override fun run() {
            Looper.prepare()
            val netData = NetData()

            val sharedPreferences: SharedPreferences = getSharedPreferences(
                "com.qhy040404.libraryonetap_preferences",
                MODE_PRIVATE
            )

            val id: String = sharedPreferences.getString("userid", "Error").toString()
            val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

            val data: String = getPortalData(id, passwd, 1)

            val remainFee = netData.getFee(data)
            val usedNet = netData.getDynamicUsedFlow(data)
            val remainNet = netData.getDynamicRemainFlow(data)
            val netMessage =
                getString(R.string.remainNetFeeAndColon) + remainFee + getString(R.string.rmb) + "\n" + getString(
                    R.string.usedNetAndColon
                ) + usedNet + getString(R.string.gigabyte) + "\n" + getString(R.string.remainNetAndColon) + remainNet + getString(
                    R.string.gigabyte
                )

            AlertDialog.Builder(this@ToolsInitActivity)
                .setMessage(netMessage)
                .setTitle(R.string.remainNet)
                .setPositiveButton(R.string.ok) { _, _ ->
                }
                .setCancelable(true)
                .create()
                .show()
            Looper.loop()
        }
    }

    private inner class getElectric : Runnable {
        override fun run() {
            Looper.prepare()
            val electricData = ElectricData()

            val sharedPreferences: SharedPreferences = getSharedPreferences(
                "com.qhy040404.libraryonetap_preferences",
                MODE_PRIVATE
            )

            val id: String = sharedPreferences.getString("userid", "Error").toString()
            val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

            val data: String = getPortalData(id, passwd, 0)

            val SSMC = electricData.getSSMC(data)
            val remainElectric = electricData.getResele(data)
            val electricMessage =
                SSMC + "\n" + getString(R.string.remainElectricAndColon) + remainElectric + getString(
                    R.string.degree
                )

            AlertDialog.Builder(this@ToolsInitActivity)
                .setMessage(electricMessage)
                .setTitle(R.string.remainElectric)
                .setPositiveButton(R.string.ok) { _, _ ->
                }
                .setCancelable(true)
                .create()
                .show()
            Looper.loop()
        }
    }
}