package com.qhy040404.libraryonetap.tools

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.R

class ToolsInitActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tools_init)
    }

    fun buttonBath(view: View) {
        val intent = Intent(this,BathReserveActivity::class.java)
        startActivity(intent)
    }

    fun buttonNet(view: View) {
        val temp = "Temp"
        AlertDialog.Builder(this)
            .setMessage(temp)
            .setTitle(R.string.vcardTitle)
            .setPositiveButton(R.string.ok) { _, _ ->
            }
            .setCancelable(false)
            .create()
            .show()
    }

    fun buttonVCard(view: View) {
        val intent = Intent(this,VCardActivity::class.java)
        startActivity(intent)
    }
}