package com.qhy040404.libraryonetap.tools

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
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
        val intent = Intent(this, BathReserveActivity::class.java)
        startActivity(intent)
    }

    fun buttonNet(view: View) {
        val netData = NetData()

        val sharedPreferences: SharedPreferences = getSharedPreferences(
            "com.qhy040404.libraryonetap_preferences",
            MODE_PRIVATE
        )

        val id: String = sharedPreferences.getString("userid", "Error").toString()
        val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

        val data: String = getPortalData(id, passwd, 1)

        val remainNet = netData.getDynamicRemainFlow(data)

        AlertDialog.Builder(this)
            .setMessage("剩余流量: $remainNet")
            .setTitle(R.string.remainNet)
            .setPositiveButton(R.string.ok) { _, _ ->
            }
            .setCancelable(false)
            .create()
            .show()
    }

    fun buttonElectric(view: View) {
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

        AlertDialog.Builder(this)
            .setMessage("$SSMC 剩余电量: $remainElectric")
            .setTitle(R.string.remainElectric)
            .setPositiveButton(R.string.ok) { _, _ ->
            }
            .setCancelable(false)
            .create()
            .show()
    }

    fun buttonVCard(view: View) {
        val intent = Intent(this, VCardActivity::class.java)
        startActivity(intent)
    }
}