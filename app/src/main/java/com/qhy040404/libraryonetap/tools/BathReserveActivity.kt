package com.qhy040404.libraryonetap.tools

import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.R

class BathReserveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bath_reserve)

        initView()
    }

    override fun onResume() {
        super.onResume()
        Thread(BathReserve()).start()
    }

    private fun initView() {
        val textViewBath: TextView = findViewById(R.id.textView3)
        textViewBath.visibility = View.VISIBLE
    }

    private inner class BathReserve : Runnable {
        override fun run() {
            Looper.prepare()
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

            val spinner: Spinner = findViewById(R.id.spinner2)
            val reserve: Button = findViewById(R.id.button15)
            val textViewBath: TextView = findViewById(R.id.textView3)

            var targetRoom = "未选中"
            ArrayAdapter.createFromResource(
                this@BathReserveActivity,
                R.array.placeArray,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            reserve.setOnClickListener {
                textViewBath.text = targetRoom
                Looper.loop()
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    targetRoom = spinner.selectedItem.toString()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }
}