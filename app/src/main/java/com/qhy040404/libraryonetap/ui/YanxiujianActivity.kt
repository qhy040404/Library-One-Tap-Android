package com.qhy040404.libraryonetap.ui

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.datamodel.OrderListData
import com.qhy040404.libraryonetap.datamodel.SessionData
import com.qhy040404.libraryonetap.ui.templates.StartUpActivity
import com.qhy040404.libraryonetap.utils.des.desEncrypt
import com.qhy040404.libraryonetap.utils.web.Requests
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

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
            val checkSession = SessionData()
            val orderList = OrderListData()

            val id: String = GlobalValues.id
            val passwd: String = GlobalValues.passwd

            var loginSuccess = false
            var timer = 0
            while (!loginSuccess) {
                val ltResponse: String = requests.get(URLManager.LIBRARY_SSO_URL)
                val ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

                val rawData = "$id$passwd$ltData"
                val rsa: String = des.strEnc(rawData, "1", "2", "3")

                requests.post(
                    URLManager.LIBRARY_SSO_URL,
                    requests.loginPostData(id, passwd, ltData, rsa),
                    GlobalValues.ctSso
                )

                val session: String =
                    requests.post(URLManager.LIBRARY_SESSION_URL, "", GlobalValues.ctSso)
                if (checkSession.isSuccess(session)) {
                    Toast.makeText(this@YanxiujianActivity, R.string.loaded, Toast.LENGTH_LONG)
                        .show()
                    loginSuccess = true
                } else {
                    Toast.makeText(this@YanxiujianActivity, R.string.logFail, Toast.LENGTH_LONG)
                        .show()
                    timer++
                    if (timer >= 3) {
                        AlertDialog.Builder(this@YanxiujianActivity)
                            .setMessage(R.string.failTimes)
                            .setTitle(R.string.error)
                            .setPositiveButton(R.string.ok) { _, _ -> this@YanxiujianActivity.finish() }
                            .setCancelable(false)
                            .create()
                            .show()
                        Looper.loop()
                        break
                    }
                }
            }
            val list = requests.get(URLManager.LIBRARY_ORDER_LIST_URL)
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

                val request = Request.Builder().url(URLManager.LIBRARY_QR_CERT_URL).build()
                val call = requests.client.newCall(request)
                call.enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {}

                    override fun onResponse(call: Call, response: Response) {
                        val picture_bt = response.body!!.bytes()
                        val pictureInput = response.body!!.byteStream()
                        val bitmap = BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                        imageView2.post { imageView2.setImageBitmap(bitmap) }
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
                    .setPositiveButton(R.string.ok) { _, _ -> this@YanxiujianActivity.finish() }
                    .setCancelable(false)
                    .create()
                    .show()
                Looper.loop()
            }
        }
    }
}