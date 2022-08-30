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
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.databinding.ActivityVcardBinding
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.isValid
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

        binding.vcardBalance.visibility = View.VISIBLE
        lifecycleScope.launch(Dispatchers.IO) {
            vCard()
        }.also {
            it.start()
        }
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
        loading.post { loading.visibility = View.INVISIBLE }
        qrView.post { qrView.setImageBitmap(QRUtils.toGrayscale(bitmap)) }
        balance.post { balance.text = qrInformation }
        refresh.post {
            refresh.setOnClickListener {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
                )
                val newQrPage = Requests.getVCard(qrUrl)
                val newQrInformation = newQrPage.split("<p class=\"bdb\">")[1].split("</p>")[0]

                @Suppress("SpellCheckingInspection")
                val newQrBase64 = newQrPage
                    .split("<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,")[1]
                    .split("\">")[0]
                val newQr = Base64.decode(newQrBase64, Base64.DEFAULT)
                val newBitmap = BitmapFactory.decodeByteArray(newQr, 0, newQr.size)
                qrView.post { qrView.setImageBitmap(QRUtils.toGrayscale(newBitmap)) }
                balance.text = newQrInformation
            }
        }
    }
}
