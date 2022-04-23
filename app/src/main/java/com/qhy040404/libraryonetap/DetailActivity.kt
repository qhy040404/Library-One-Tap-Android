package com.qhy040404.libraryonetap

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.des.desEncrypt
import com.qhy040404.libraryonetap.web.CheckSession
import com.qhy040404.libraryonetap.web.Requests
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

            val requests: Requests = Requests()
            val des: desEncrypt = desEncrypt()
            val checkSession: CheckSession = CheckSession()

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
                var ltResponse: String = requests.get(requestUrl, id)
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
                            .setPositiveButton("好",DialogInterface.OnClickListener { _, _ ->
                                exitProcess(1)
                            })
                            .setCancelable(false)
                            .create()
                            .show()
                        Looper.loop()
                        break
                    }
                }
            }
        }
    }
}