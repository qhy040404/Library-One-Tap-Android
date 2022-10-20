package com.qhy040404.libraryonetap.ui.tools

import android.graphics.BitmapFactory
import android.os.StrictMode
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.model.VCardStatusClass
import com.qhy040404.libraryonetap.databinding.ActivityVcardBinding
import com.qhy040404.libraryonetap.utils.QRUtils
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.isValid
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

@Suppress("SpellCheckingInspection")
class VCardActivity : BaseActivity<ActivityVcardBinding>() {
    private var isActivityVisible = false

    override fun init() {
        setSupportActionBar(binding.toolbar)
        (binding.root as ViewGroup).bringChildToFront(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.title = getString(R.string.vcard_title)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }
        isActivityVisible = true

        binding.vcardBalance.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            vCard()
        }.also {
            it.start()
        }
    }

    override fun onResume() {
        super.onResume()
        isActivityVisible = true
    }

    override fun onStop() {
        super.onStop()
        isActivityVisible = false
    }

    private suspend fun vCard() {
        val qrView = binding.vcardQr
        val balance = binding.vcardBalance
        val refresh = binding.vcardRefresh
        val loading = binding.vcardLoading

        val id = GlobalValues.id
        val passwd = GlobalValues.passwd

        @Suppress("SpellCheckingInspection")
        val apiPostData = "schoolcode=dlut&username=$id&password=$passwd&ssokey="
        Requests.postVCard(URLManager.VCARD_API_URL, apiPostData, GlobalValues.ctVCard)

        val openidOrigin = Requests.getVCard(URLManager.VCARD_OPENID_URL)
        if (!openidOrigin.isValid()) {
            withContext(Dispatchers.Main)
            {
                MaterialAlertDialogBuilder(this@VCardActivity)
                    .setTitle(R.string.vcard_title)
                    .setMessage(when (openidOrigin) {
                        Constants.NET_DISCONNECTED -> R.string.net_disconnected
                        Constants.NET_ERROR -> R.string.net_error
                        Constants.NET_TIMEOUT -> R.string.net_timeout
                        else -> R.string.unknown_error
                    })
                    .setPositiveButton(R.string.ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            }
            return
        }

        val openid = try {
            openidOrigin
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

        var qrPage = Requests.getVCard(qrUrl)
        var qrInformation = qrPage
            .split("<p class=\"bdb\">")[1]
            .split("</p>")[0]

        var qrBase64 = qrPage
            .split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1]
            .split("\">")[0]
        var payCode = qrPage
            .split("<input id=\"code\" value=\"")[1]
            .split("\" type=\"hidden\">")[0]

        var qr = Base64.decode(qrBase64, Base64.DEFAULT)
        var bitmap = BitmapFactory.decodeByteArray(qr, 0, qr.size)
        loading.post { loading.visibility = View.INVISIBLE }
        qrView.load(QRUtils.toGrayscale(bitmap))
        balance.post { balance.text = qrInformation }
        refresh.post {
            refresh.setOnClickListener {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
                )
                qrPage = Requests.getVCard(qrUrl)
                qrInformation = qrPage
                    .split("<p class=\"bdb\">")[1]
                    .split("</p>")[0]

                qrBase64 = qrPage
                    .split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1]
                    .split("\">")[0]
                payCode = qrPage
                    .split("<input id=\"code\" value=\"")[1]
                    .split("\" type=\"hidden\">")[0]

                qr = Base64.decode(qrBase64, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(qr, 0, qr.size)
                qrView.load(QRUtils.toGrayscale(bitmap))
                balance.text = qrInformation
            }
        }
        thread {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            )
            while (isActivityVisible) {
                Thread.sleep(3000L)
                val statusOrig =
                    Requests.getVCard(URLManager.getVCardCheckUrl(openid, payCode)).trim()
                val status = moshi.adapter(VCardStatusClass::class.java).fromJson(statusOrig)!!
                if (status.resultData.status != "5" && isActivityVisible) {
                    runOnUiThread {
                        refresh.performClick()
                    }
                }
            }
        }
    }
}
