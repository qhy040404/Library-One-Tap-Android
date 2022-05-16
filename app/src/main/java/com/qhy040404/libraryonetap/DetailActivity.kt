package com.qhy040404.libraryonetap

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.qhy040404.libraryonetap.data.CancelData
import com.qhy040404.libraryonetap.data.OrderList
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.web.CheckSession
import com.qhy040404.libraryonetap.web.Requests
import okhttp3.*
import java.io.IOException
import kotlin.system.exitProcess

class DetailActivity : StartUpActivity() {
    override fun init() = initView()

    override fun getLayoutId(): Int = R.layout.activity_detail

    private fun initView() {
        val textView: TextView = findViewById(R.id.textView)
        textView.visibility = View.VISIBLE
        Thread(Detail()).start()
    }

    private inner class Detail : Runnable {
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

            val textView: TextView = findViewById(R.id.textView)
            val leave: Button = findViewById(R.id.button4)
            val tempLeave: Button = findViewById(R.id.button5)
            val enter: Button = findViewById(R.id.button6)
            val imageView: ImageView = findViewById(R.id.imageView)
            val refresh: Button = findViewById(R.id.button7)
            val cancel: Button = findViewById(R.id.button10)

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
                        Toast.makeText(this@DetailActivity, R.string.loaded, Toast.LENGTH_LONG)
                    makeText.show()
                    loginSuccess = true
                } else {
                    val makeText =
                        Toast.makeText(this@DetailActivity, R.string.logFail, Toast.LENGTH_LONG)
                    makeText.show()
                    timer++
                    if (timer >= 3) {
                        AlertDialog.Builder(this@DetailActivity)
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
                val back_prompt = getString(R.string.tempEndTime)

                val space_name = orderList.getSpace_name(list, "2")
                val seat_label = orderList.getSeat_label(list, "2")
                val order_date = orderList.getOrder_date(list, "2")
                var order_id = orderList.getOrder_id(list, "2")
                var order_process = orderList.getOrder_process(list, "2")
                val back_time = orderList.getBack_time(list, "2", back_prompt)

                if (order_id.equals("oid")) {
                    order_id = getString(R.string.noValidOrder)
                }

                if (order_process.equals("审核通过")) {
                    order_process = getString(R.string.notStart)

                    cancel.post {
                        cancel.visibility = View.VISIBLE
                        cancel.isClickable = true
                    }
                } else if (order_process.equals("进行中")) {
                    order_process = getString(R.string.inside)
                } else if (order_process.equals("暂离")) {
                    order_process = getString(R.string.outside)
                }

                enter.setOnClickListener {
                    val method = "in"
                    val qrCodeUrl =
                        "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$order_id"
                    val request = Request.Builder().url(qrCodeUrl).build()
                    val call = requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val picture_bt = response.body!!.bytes()
                            val pictureInput = response.body!!.byteStream()
                            val bitmap =
                                BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                            imageView.post {
                                imageView.setImageBitmap(bitmap)
                            }
                            pictureInput.close()
                        }
                    })
                }
                leave.setOnClickListener {
                    val method = "out"
                    val qrCodeUrl =
                        "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$order_id"
                    val request = Request.Builder().url(qrCodeUrl).build()
                    val call = requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val picture_bt = response.body!!.bytes()
                            val pictureInput = response.body!!.byteStream()
                            val bitmap =
                                BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                            imageView.post {
                                imageView.setImageBitmap(bitmap)
                            }
                            pictureInput.close()
                        }
                    })
                }
                tempLeave.setOnClickListener {
                    val method = "temp"
                    val qrCodeUrl =
                        "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$order_id"
                    val request = Request.Builder().url(qrCodeUrl).build()
                    val call = requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {

                        }

                        override fun onResponse(call: Call, response: Response) {
                            val picture_bt = response.body!!.bytes()
                            val pictureInput = response.body!!.byteStream()
                            val bitmap =
                                BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                            imageView.post {
                                imageView.setImageBitmap(bitmap)
                            }
                            pictureInput.close()
                        }
                    })
                }
                refresh.setOnClickListener { recreate() }
                cancel.setOnClickListener {
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
                    AlertDialog.Builder(this@DetailActivity)
                        .setMessage(R.string.confirmCancel)
                        .setTitle(R.string.library)
                        .setPositiveButton(R.string.justCancel) { _, _ ->
                            val cancelUrl =
                                "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderOperation"
                            val message = CancelData().getMessage(
                                requests.post(
                                    cancelUrl,
                                    "order_id=$order_id&order_type=2&method=Cancel",
                                    ctLibrary
                                )
                            )
                            AlertDialog.Builder(this@DetailActivity)
                                .setMessage(message)
                                .setTitle(R.string.library)
                                .setPositiveButton(R.string.ok) { _, _ ->
                                    recreate()
                                }
                                .setCancelable(true)
                                .create()
                                .show()
                        }
                        .setNegativeButton(R.string.cancelCancel) { _, _ ->
                        }
                        .setCancelable(true)
                        .create()
                        .show()
                }
                textView.text =
                    "order_id: $order_id\n\n$order_process\n\n$space_name\n$seat_label\n$order_date\n$back_time"
                Looper.loop()
            } else {
                AlertDialog.Builder(this@DetailActivity)
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