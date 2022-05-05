package com.qhy040404.libraryonetap.tools

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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
}