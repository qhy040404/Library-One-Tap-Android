package com.qhy040404.libraryonetap.tools

import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Looper
import android.os.StrictMode
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.utils.QRUtils
import com.qhy040404.libraryonetap.web.Requests
import okhttp3.MediaType

class VCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vcard)

        initView()
    }

    override fun onResume() {
        super.onResume()
        Thread(VCard()).start()
    }

    private fun initView() {
        val textView: TextView = findViewById(R.id.textView4)
        textView.visibility = View.VISIBLE
    }

    private inner class VCard : Runnable {
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
            val requests = Requests()
            val qrUtils = QRUtils()

            val imageView: ImageView = findViewById(R.id.imageView3)
            val textView: TextView = findViewById(R.id.textView4)
            val refresh: Button = findViewById(R.id.button18)

            val sharedPreferences: SharedPreferences =
                getSharedPreferences("com.qhy040404.libraryonetap_preferences", MODE_PRIVATE)

            val id: String = sharedPreferences.getString("userid", "Error").toString()
            val passwd: String = sharedPreferences.getString("passwd", "Error").toString()

            val ct: MediaType = requests.strToMT("application/x-www-form-urlencoded")

            val apiRequestUrl =
                "https://api.m.dlut.edu.cn/login?redirect_uri=https://card.m.dlut.edu.cn/homerj/openHomePage&response_type=code&scope=base_api&state=weishao"
            val apiPostData = "schoolcode=dlut&username=$id&password=$passwd&ssokey="
            requests.postVCard(apiRequestUrl, apiPostData, ct)

            val openidUrl = "https://card.m.dlut.edu.cn/homerj/openRjOAuthPage"
            val returnOpenID = requests.getVCard(openidUrl)
            val openid =
                returnOpenID.split("<input id=\"openid\" value=\"")[1].split("\" type=\"hidden\">")[0]

            val qrUrl =
                "https://card.m.dlut.edu.cn/virtualcard/openVirtualcard?openid=$openid&displayflag=1&id=19"

            val qrPage = requests.getVCard(qrUrl)
            val qrInformation = qrPage.split("<p class=\"bdb\">")[1].split("</p>")[0]
            val qrBase64 =
                qrPage.split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1].split(
                    "\">"
                )[0]
            val qr = Base64.decode(qrBase64, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(qr, 0, qr.size)
            imageView.post {
                imageView.setImageBitmap(qrUtils.toGrayscale(bitmap))
            }
            textView.text = qrInformation
            refresh.setOnClickListener {
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
                val newQrPage = requests.getVCard(qrUrl)
                val newQrInformation = qrPage.split("<p class=\"bdb\">")[1].split("</p>")[0]
                val newQrBase64 =
                    newQrPage.split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1].split(
                        "\">"
                    )[0]
                val newQr = Base64.decode(newQrBase64, Base64.DEFAULT)
                val newBitmap = BitmapFactory.decodeByteArray(newQr, 0, newQr.size)
                imageView.post {
                    imageView.setImageBitmap(qrUtils.toGrayscale(newBitmap))
                }
                textView.post {
                    textView.text = newQrInformation
                }
            }
            Looper.loop()
        }
    }
}