package com.qhy040404.libraryonetap

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.data.OrderList
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.web.CheckSession
import com.qhy040404.libraryonetap.web.Requests
import okhttp3.*
import java.io.IOException
import kotlin.system.exitProcess

class YanxiujianActivity : StartUpActivity() {
    override fun init() = initView()

    override fun getLayoutId(): Int = R.layout.activity_yanxiujian

    private fun initView() {
        val textView2: TextView = findViewById(R.id.textView2)
        textView2.visibility = View.VISIBLE
        Thread(Yanxiujian()).start()
    }

    private inner class Yanxiujian : Runnable {
        @SuppressLint("SetTextI18n")
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

            val textView2: TextView = findViewById(R.id.textView2)
            val imageView2: ImageView = findViewById(R.id.imageView2)
            val refresh2: Button = findViewById(R.id.button12)

            val requests = Requests()
            val des = desEncrypt()
            val checkSession = CheckSession()
            val orderList = OrderList()

            val sharedPreferences: SharedPreferences =
                getSharedPreferences("com.qhy040404.libraryonetap_preferences", MODE_PRIVATE)

            val id: String = sharedPreferences.getString("userid", "Error").toString()
            val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

            val ctLibrary: MediaType =
                requests.strToMT("application/x-www-form-urlencoded; charset=utf-8")

            val requestUrl =
                "https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php"
            val sessionUrl =
                "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=checkSession"
            var loginSuccess = false
            var timer = 0
            while (!loginSuccess) {
                val ltResponse: String = requests.get(requestUrl)
                val ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

                val rawData = "$id$passwd$ltData"
                val rsa: String = des.strEnc(rawData, "1", "2", "3")

                requests.post(
                    requestUrl,
                    requests.loginPostData(id, passwd, ltData, rsa),
                    ctLibrary
                )

                val session: String = requests.post(sessionUrl, "", ctLibrary)
                if (checkSession.isSuccess(session)) {
                    val makeText =
                        Toast.makeText(this@YanxiujianActivity, R.string.loaded, Toast.LENGTH_LONG)
                    makeText.show()
                    loginSuccess = true
                } else {
                    val makeText =
                        Toast.makeText(this@YanxiujianActivity, R.string.logFail, Toast.LENGTH_LONG)
                    makeText.show()
                    timer++
                    if (timer >= 3) {
                        AlertDialog.Builder(this@YanxiujianActivity)
                            .setMessage(R.string.failTimes)
                            .setTitle(R.string.error)
                            .setPositiveButton(R.string.ok) { _, _ ->
                                exitProcess(1)
                            }
                            .setCancelable(false)
                            .create()
                            .show()
                        Looper.loop()
                        break
                    }
                }
            }
            val listUrl =
                "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderList&order=asc&offset=0&limit=10"
            val list = requests.get(listUrl)
            val total = orderList.getTotal(list)
            if (!total.equals("0")) {
                val space_name = orderList.getSpace_name(list, "1")
                val order_date = orderList.getOrder_date(list, "1")
                var order_id = orderList.getOrder_id(list, "1")
                var order_process = orderList.getOrder_process(list, "1")
                val all_users = orderList.getAll_users(list)
                val full_time = orderList.getFull_time(list)

                if (order_id.equals("oid")) {
                    order_id = getString(R.string.noValidOrder)
                }

                if (order_process.equals("审核通过")) {
                    order_process = getString(R.string.notStart)
                } else if (order_process.equals("进行中")) {
                    order_process = getString(R.string.inside)
                } else if (order_process.equals("暂离")) {
                    order_process = getString(R.string.outside)
                }

                val qrUrl = "http://seat.lib.dlut.edu.cn/yanxiujian/client/2codecert.php?"
                val request = Request.Builder().url(qrUrl).build()
                val call = requests.client.newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        val picture_bt = response.body!!.bytes()
                        val pictureInput = response.body!!.byteStream()
                        val bitmap = BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                        imageView2.post {
                            imageView2.setImageBitmap(bitmap)
                        }
                        pictureInput.close()
                    }
                })
                refresh2.setOnClickListener { recreate() }
                textView2.text =
                    "order_id: $order_id\n\n$order_process\n\n$space_name\n$order_date\n$full_time\n\n$all_users"
                Looper.loop()
            } else {
                AlertDialog.Builder(this@YanxiujianActivity)
                    .setMessage(R.string.loginTimeout)
                    .setTitle(R.string.error)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        exitProcess(1)
                    }
                    .setCancelable(false)
                    .create()
                    .show()
                Looper.loop()
            }
        }
    }
}