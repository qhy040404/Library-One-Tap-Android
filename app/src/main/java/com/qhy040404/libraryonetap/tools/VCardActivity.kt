package com.qhy040404.libraryonetap.tools

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.R

class VCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vcard)

        initView()
    }

    private fun initView() {
        val textView: TextView = findViewById(R.id.textView4)
        textView.visibility = View.VISIBLE
    }
}