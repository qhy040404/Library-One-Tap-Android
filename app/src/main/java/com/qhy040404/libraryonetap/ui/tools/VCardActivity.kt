package com.qhy040404.libraryonetap.ui.tools

import android.graphics.BitmapFactory
import android.os.StrictMode
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.databinding.ActivityVcardBinding
import com.qhy040404.libraryonetap.utils.tools.QRUtils
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        binding.textView4.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            vCard()
        }.also {
            it.start()
        }
    }

    private suspend fun vCard() {
        val imageView = binding.imageView3
        val textView = binding.textView4
        val refresh = binding.button18
        val progressBar = binding.progressBar3

        val id = GlobalValues.id
        val passwd = GlobalValues.passwd

        @Suppress("SpellCheckingInspection")
        val apiPostData = "schoolcode=dlut&username=$id&password=$passwd&ssokey="
        Requests.postVCard(URLManager.VCARD_API_URL, apiPostData, GlobalValues.ctVCard)

        val openid = try {
            Requests.getVCard(URLManager.VCARD_OPENID_URL)
                .split("<input id=\"openid\" value=\"")[1]
                .split("\" type=\"hidden\">")[0]
        } catch (_: Exception) {
            withContext(Dispatchers.Main)
            {
                MaterialAlertDialogBuilder(this@VCardActivity)
                    .setTitle(R.string.vcard_title)
                    .setMessage(R.string.fail_to_login_three_times)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            }
            return
        }

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