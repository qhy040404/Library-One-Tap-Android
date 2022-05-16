package com.qhy040404.libraryonetap.tools

import android.content.SharedPreferences
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.*
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.StartUpActivity
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.tools.utils.BathTime.getBathTime
import com.qhy040404.libraryonetap.web.Requests
import okhttp3.MediaType

class BathReserveActivity : StartUpActivity() {
    override fun init() = initView()

    override fun getLayoutId(): Int = R.layout.activity_bath_reserve

    private fun initView() {
        val textViewBath: TextView = findViewById(R.id.textView3)
        textViewBath.visibility = View.VISIBLE
        Thread(BathReserve()).start()
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

            var targetRoom = 20
            ArrayAdapter.createFromResource(
                this@BathReserveActivity,
                R.array.placeArray,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }

            val requests = Requests()
            val des = desEncrypt()

            val sharedPreferences: SharedPreferences = getSharedPreferences(
                "com.qhy040404.libraryonetap_preferences",
                MODE_PRIVATE
            )
            val id: String = sharedPreferences.getString("userid", "Error").toString()
            val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

            val ctsso: MediaType =
                requests.strToMT("application/x-www-form-urlencoded; charset=utf-8")

            val bathLoginUrl =
                "https://sso.dlut.edu.cn/cas/login?service=http%3A%2F%2F202.118.74.5%3A8193%2FopenHomeRJPage"
            val saveUrl = "http://202.118.74.5:8193/goods/saveGoodsShopcar"
            val cartUrl = "http://202.118.74.5:8193/goods/updateGoodsShopcar"
            val mainUrl = "http://202.118.74.5:8193/goods/queryNoChoiceTimeInfo"
            val payUrl = "http://202.118.74.5:8193/payAdvanceOrder"

            val time = getBathTime()

            val ltResponse: String = requests.get(bathLoginUrl)
            val ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

            val rawData = "$id$passwd$ltData"
            val rsa: String = des.strEnc(rawData, "1", "2", "3")

            requests.post(
                bathLoginUrl,
                requests.loginPostData(id, passwd, ltData, rsa),
                ctsso
            )

            textViewBath.text = getString(R.string.loaded)

            reserve.setOnClickListener {
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
                val savePostData = "mealorder=0&goodsid=$targetRoom&goodsnum=1&addlocation=1"
                val cartPostData = "goodsShopcarId=$targetRoom&rulesid=$time"
                val mainPostData = "goodsid=$targetRoom%2C&ruleid=$time"
                val payPostData = "goodis=$targetRoom&payway=nopay"

                requests.post(saveUrl, savePostData, ctsso)
                requests.post(cartUrl, cartPostData, ctsso)
                requests.post(mainUrl, mainPostData, ctsso)
                requests.post(payUrl, payPostData, ctsso)
                textViewBath.text = getString(R.string.sentRequest)
                Looper.loop()
            }

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    when (spinner.selectedItem.toString()) {
                        "西山1楼", "West 1F" -> {
                            targetRoom = 20
                        }
                        "西山2楼", "West 2F" -> {
                            targetRoom = 21
                        }
                        "西山3楼", "West 3F" -> {
                            targetRoom = 17
                        }
                        "北山男", "North Male" -> {
                            targetRoom = 18
                        }
                        "北山女", "North Female" -> {
                            targetRoom = 19
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            }

        }
    }
}