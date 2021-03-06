package com.qhy040404.libraryonetap.ui.tools

import android.graphics.BitmapFactory
import android.os.StrictMode
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.databinding.ActivityVcardBinding
import com.qhy040404.libraryonetap.utils.tools.QRUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VCardActivity : BaseActivity<ActivityVcardBinding>() {
    override fun init() = initView()

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        (binding.root as ViewGroup).bringChildToFront(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.vcard_title)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }

        val textView: TextView = findViewById(R.id.textView4)
        textView.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            vCard()
        }.also {
            it.start()
        }
    }

    private fun vCard() {
        val imageView: ImageView = findViewById(R.id.imageView3)
        val textView: TextView = findViewById(R.id.textView4)
        val refresh: Button = findViewById(R.id.button18)
        val progressBar: ProgressBar = findViewById(R.id.progressBar3)

        val id: String = GlobalValues.id
        val passwd: String = GlobalValues.passwd

        @Suppress("SpellCheckingInspection")
        val apiPostData = "schoolcode=dlut&username=$id&password=$passwd&ssokey="
        Requests.postVCard(URLManager.VCARD_API_URL, apiPostData, GlobalValues.ctVCard)

        val openid =
            Requests.getVCard(URLManager.VCARD_OPENID_URL)
                .split("<input id=\"openid\" value=\"")[1]
                .split("\" type=\"hidden\">")[0]

        val qrUrl = URLManager.getVCardQRUrl(openid)

        val qrPage = Requests.getVCard(qrUrl)
        val qrInformation = qrPage
            .split("<p class=\"bdb\">")[1]
            .split("</p>")[0]

        @Suppress("SpellCheckingInspection")
        val qrBase64 = qrPage
            .split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1]
            .split("\">")[0]
        val qr = Base64.decode(qrBase64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(qr, 0, qr.size)
        progressBar.post { progressBar.visibility = View.INVISIBLE }
        imageView.post { imageView.setImageBitmap(QRUtils.toGrayscale(bitmap)) }
        textView.post { textView.text = qrInformation }
        refresh.post {
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
                val newQrPage = Requests.getVCard(qrUrl)
                val newQrInformation = newQrPage.split("<p class=\"bdb\">")[1].split("</p>")[0]

                @Suppress("SpellCheckingInspection")
                val newQrBase64 = newQrPage
                    .split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1]
                    .split("\">")[0]
                val newQr = Base64.decode(newQrBase64, Base64.DEFAULT)
                val newBitmap = BitmapFactory.decodeByteArray(newQr, 0, newQr.size)
                imageView.post { imageView.setImageBitmap(QRUtils.toGrayscale(newBitmap)) }
                textView.text = newQrInformation
            }
        }
    }
}