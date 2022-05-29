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
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.GlobalValues.ctSso
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.datamodel.CancelData
import com.qhy040404.libraryonetap.datamodel.OrderListData
import com.qhy040404.libraryonetap.utils.des.desEncrypt
import com.qhy040404.libraryonetap.datamodel.SessionData
import com.qhy040404.libraryonetap.utils.web.Requests
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class DetailActivity : StartUpActivity() {
    override fun init() = initView()

    override fun getLayoutId(): Int = R.layout.activity_detail

    private fun initView() {
        val textView: TextView = findViewById(R.id.textView)
        textView.visibility = View.VISIBLE
        Thread(Detail()).start()
    }

    private fun getQRUrl(method: String, id: String): String {
        return "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$id"
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
            val reserve: Button = findViewById(R.id.button11)

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
                    ctSso
                )

                val session: String = requests.post(URLManager.LIBRARY_SESSION_URL, "", ctSso)
                if (checkSession.isSuccess(session)) {
                    Toast.makeText(this@DetailActivity, R.string.loaded, Toast.LENGTH_LONG).show()
                    loginSuccess = true
                } else {
                    Toast.makeText(this@DetailActivity, R.string.logFail, Toast.LENGTH_LONG).show()
                    timer++
                    if (timer >= 3) {
                        AlertDialog.Builder(this@DetailActivity)
                            .setMessage(R.string.failTimes)
                            .setTitle(R.string.error)
                            .setPositiveButton(R.string.ok) { _, _ -> this@DetailActivity.finish() }
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
                val back_prompt = getString(R.string.tempEndTime)

                val space_name = orderList.getSpace_name(list, "2")
                val seat_label = orderList.getSeat_label(list, "2")
                val order_date = orderList.getOrder_date(list, "2")
                var order_id = orderList.getOrder_id(list, "2")
                var order_process = orderList.getOrder_process(list, "2")
                val back_time = orderList.getBack_time(list, "2", back_prompt)

                if (order_id.equals("oid")) {
                    order_id = getString(R.string.noValidOrder)
                    reserve.post {
                        reserve.visibility = View.VISIBLE
                        reserve.isClickable = true
                    }
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
                    val request =
                        Request.Builder()
                            .url(getQRUrl(Constants.LIBRARY_METHOD_IN, order_id))
                            .build()
                    val call = requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            val picture_bt = response.body!!.bytes()
                            val pictureInput = response.body!!.byteStream()
                            val bitmap =
                                BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                            imageView.post { imageView.setImageBitmap(bitmap) }
                            pictureInput.close()
                        }
                    })
                }
                leave.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(getQRUrl(Constants.LIBRARY_METHOD_OUT, order_id))
                            .build()
                    val call = requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            val picture_bt = response.body!!.bytes()
                            val pictureInput = response.body!!.byteStream()
                            val bitmap =
                                BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                            imageView.post { imageView.setImageBitmap(bitmap) }
                            pictureInput.close()
                        }
                    })
                }
                tempLeave.setOnClickListener {
                    val request =
                        Request.Builder()
                            .url(getQRUrl(Constants.LIBRARY_METHOD_TEMP, order_id))
                            .build()
                    val call = requests.client.newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {}

                        override fun onResponse(call: Call, response: Response) {
                            val picture_bt = response.body!!.bytes()
                            val pictureInput = response.body!!.byteStream()
                            val bitmap =
                                BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                            imageView.post { imageView.setImageBitmap(bitmap) }
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
                            val message = CancelData().getMessage(
                                requests.post(
                                    URLManager.LIBRARY_ORDER_CANCEL_URL,
                                    "order_id=$order_id&order_type=2&method=Cancel",
                                    ctSso
                                )
                            )
                            AlertDialog.Builder(this@DetailActivity)
                                .setMessage(message)
                                .setTitle(R.string.library)
                                .setPositiveButton(R.string.ok) { _, _ -> recreate() }
                                .setCancelable(true)
                                .create()
                                .show()
                        }
                        .setNegativeButton(R.string.cancelCancel) { _, _ -> }
                        .setCancelable(true)
                        .create()
                        .show()
                }
                reserve.setOnClickListener { ReserveDialog().showAlertDialog(this@DetailActivity) }
                textView.text =
                    "order_id: $order_id\n\n$order_process\n\n$space_name\n$seat_label\n$order_date\n$back_time"
                Looper.loop()
            } else {
                AlertDialog.Builder(this@DetailActivity)
                    .setMessage(R.string.loginTimeout)
                    .setTitle(R.string.error)
                    .setPositiveButton(R.string.ok) { _, _ -> this@DetailActivity.finish() }
                    .setCancelable(false)
                    .create()
                    .show()
                Looper.loop()
            }
        }
    }
}