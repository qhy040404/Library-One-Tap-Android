package com.qhy040404.libraryonetap

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
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
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.system.exitProcess

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initView()
    }

    override fun onResume() {
        super.onResume()
        Thread(Detail()).start()
    }

    private fun initView() {
        val textView: TextView = findViewById(R.id.textView)
        textView.visibility = View.VISIBLE
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

            val requests: Requests = Requests()
            val des: desEncrypt = desEncrypt()
            val checkSession: CheckSession = CheckSession()
            val orderList: OrderList = OrderList()

            val sharedPreferences: SharedPreferences =
                getSharedPreferences("com.qhy040404.libraryonetap_preferences", MODE_PRIVATE)

            val id: String = sharedPreferences.getString("userid", "Error").toString()
            val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

            val requestUrl: String =
                "https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php"
            val sessionUrl: String =
                "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=checkSession"
            var loginSuccess: Boolean = false
            var timer: Int = 0
            while (!loginSuccess) {
                var ltResponse: String = requests.get(requestUrl)
                var ltData: String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

                var rawData: String = "$id$passwd$ltData"
                var rsa: String = des.strEnc(rawData, "1", "2", "3")

                var login: String =
                    requests.post(requestUrl, requests.loginPostData(id, passwd, ltData, rsa))

                var session: String = requests.post(sessionUrl, "")
                if (checkSession.isSuccess(session)) {
                    val makeText = Toast.makeText(this@DetailActivity, "登录成功", Toast.LENGTH_LONG)
                    makeText.show()
                    loginSuccess = true
                } else {
                    val makeText =
                        Toast.makeText(this@DetailActivity, "登录失败，正在重试", Toast.LENGTH_LONG)
                    makeText.show()
                    timer++
                    if (timer >= 3) {
                        AlertDialog.Builder(this@DetailActivity)
                            .setMessage("连续失败3次，返回主页面\n请检查用户名和密码")
                            .setTitle("错误")
                            .setPositiveButton("好", DialogInterface.OnClickListener { _, _ ->
                                exitProcess(1)
                            })
                            .setCancelable(false)
                            .create()
                            .show()
                        break
                    }
                }
            }
            val listUrl =
                "http://seat.lib.dlut.edu.cn/yanxiujian/client/orderRoomAction.php?action=myOrderList&order=asc&offset=0&limit=10"
            val list = requests.get(listUrl)
            val total = orderList.getTotal(list)
            if (!total.equals("0")) {
                val space_name = orderList.getSpace_name(list)
                val seat_label = orderList.getSeat_label(list)
                val order_date = orderList.getOrder_date(list)
                val order_id = orderList.getOrder_id(list)
                var order_process = orderList.getOrder_process(list)

                if (order_process.equals("审核通过")) {
                    order_process = "未开始"
                }

                enter.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        var method = "in"
                        val QrCodeUrl =
                            "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$order_id" //具体参数明天看
                        var request = Request.Builder().url(QrCodeUrl).build()
                        var call = requests.client.newCall(request)
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {

                            }

                            override fun onResponse(call: Call, response: Response) {
                                var picture_bt = response.body!!.bytes()
                                var pictureInput = response.body!!.byteStream()
                                var bitmap =
                                    BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                                imageView.post(Runnable {
                                    imageView.setImageBitmap(bitmap)
                                })
                                //imageView.setImageBitmap(bitmap)
                                pictureInput.close()
                            }
                        })
                    }
                })
                leave.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        var method = "out"
                        val QrCodeUrl =
                            "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$order_id" //具体参数明天看
                        var request = Request.Builder().url(QrCodeUrl).build()
                        var call = requests.client.newCall(request)
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {

                            }

                            override fun onResponse(call: Call, response: Response) {
                                var picture_bt = response.body!!.bytes()
                                var pictureInput = response.body!!.byteStream()
                                var bitmap =
                                    BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                                imageView.post(Runnable {
                                    imageView.setImageBitmap(bitmap)
                                })
                                //imageView.setImageBitmap(bitmap)
                                pictureInput.close()
                            }
                        })
                    }
                })
                tempLeave.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        var method = "temp"
                        val QrCodeUrl =
                            "http://seat.lib.dlut.edu.cn/yanxiujian/client/2code.php?method=$method&order_id=$order_id" //具体参数明天看
                        var request = Request.Builder().url(QrCodeUrl).build()
                        var call = requests.client.newCall(request)
                        call.enqueue(object : Callback {
                            override fun onFailure(call: Call, e: IOException) {

                            }

                            override fun onResponse(call: Call, response: Response) {
                                var picture_bt = response.body!!.bytes()
                                var pictureInput = response.body!!.byteStream()
                                var bitmap =
                                    BitmapFactory.decodeByteArray(picture_bt, 0, picture_bt.size)
                                imageView.post(Runnable {
                                    imageView.setImageBitmap(bitmap)
                                })
                                //imageView.setImageBitmap(bitmap)
                                pictureInput.close()
                            }
                        })
                    }
                })
                refresh.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(p0: View?) {
                        recreate()
                    }
                })
                textView.text =
                    "order_id:$order_id\n\n$order_process\n\n$space_name\n$seat_label\n$order_date"
                Looper.loop()
            } else {
                AlertDialog.Builder(this@DetailActivity)
                    .setMessage("登录失效，请重新进入此页面")
                    .setTitle("错误")
                    .setPositiveButton("好", DialogInterface.OnClickListener { _, _ ->
                        exitProcess(1)
                    })
                    .setCancelable(false)
                    .create()
                    .show()
            }
        }
    }
}