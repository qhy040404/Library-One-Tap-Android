package com.qhy040404.libraryonetap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class YanxiujianActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yanxiujian)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
    }

    override fun onResume() {
        super.onResume()
        Thread(Yanxiujian()).start()
    }

    private fun initView() {

    }

    private inner class Yanxiujian : Runnable {
        override fun run() {

        }
    }
}