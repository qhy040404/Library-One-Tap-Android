package com.qhy040404.libraryonetap.ui.tools

import android.graphics.Bitmap
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
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.VCardStatusDTO
import com.qhy040404.libraryonetap.databinding.ActivityVcardBinding
import com.qhy040404.libraryonetap.utils.QRUtils
import com.qhy040404.libraryonetap.utils.encrypt.AesEncryptUtils
import com.qhy040404.libraryonetap.utils.extensions.AnyExtensions.toJson
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.decode
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.isValid
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
                    .setMessage(
                        when (openidOrigin) {
                            Constants.NET_DISCONNECTED -> R.string.glb_net_disconnected
                            Constants.NET_ERROR -> R.string.glb_net_error
                            Constants.NET_TIMEOUT -> R.string.glb_net_timeout
                            else -> R.string.glb_unknown_error
                        }
                    )
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            }
            return
        }

        val openid = try {
            openidOrigin.substringBetween("<input id=\"openid\" value=\"", "\" type=\"hidden\">")
        } catch (_: Exception) {
            withContext(Dispatchers.Main)
            {
                MaterialAlertDialogBuilder(this@VCardActivity)
                    .setTitle(R.string.vcard_title)
                    .setMessage(R.string.glb_fail_to_login_three_times)
                    .setPositiveButton(R.string.glb_ok) { _, _ ->
                        finish()
                    }
                    .setCancelable(false)
                    .create()
                    .show()
            }
            return
        }

        var payCode = ""
        updateCode(openid).let {
            runOnUiThread {
                loading.visibility = View.INVISIBLE
                qrView.load(QRUtils.toGrayscale(it.second))
                balance.text = it.first
                payCode = it.third
            }
        }
        runOnUiThread {
            refresh.setOnClickListener {
                StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder().permitAll().build()
                )

                updateCode(openid).let {
                    qrView.load(QRUtils.toGrayscale(it.second))
                    balance.text = it.first
                    payCode = it.third
                }
            }
        }
        thread {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder().permitAll().build()
            )
            while (isActivityVisible) {
                Thread.sleep(3000L)
                val targetUrl = URLManager.getVCardCheckUrl(openid)
                val params = mapOf(
                    "paycode" to payCode,
                    "openid" to openid
                ).toJson()!!

                val key = AesEncryptUtils.VCard.genKey(16)
                val encryptedParams = mapOf(
                    "datajson" to (AesEncryptUtils.VCard.handleKey(
                        key,
                        true
                    ) + AesEncryptUtils.VCard.encrypt(
                        params,
                        key
                    )).replace("\n", "")
                ).toJson()!!

                Requests.postVCard(targetUrl, encryptedParams, GlobalValues.ctJson)
                    .let { response ->
                        JSONObject(response).optString("datajson").let { data ->
                            AesEncryptUtils.VCard.decrypt(
                                data.substring(16),
                                AesEncryptUtils.VCard.handleKey(
                                    data.substring(0, 16),
                                    false
                                )
                            ).decode<VCardStatusDTO>()?.let { result ->
                                if (result.resultData.status != "5" && isActivityVisible) {
                                    runOnUiThread {
                                        refresh.performClick()
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun updateCode(openid: String): Triple<String, Bitmap, String> {
        val payData = Requests.getVCard(URLManager.getVCardQRUrl(openid)).let {
            Triple(
                it.substringBetween("<p class=\"bdb\">", "</p>"),
                it.substringBetween(
                    "<img id=\"qrcode\" onclick=\"refreshPaycode();\" src=\"data:image/png;base64,",
                    "\">"
                ),
                it.substringBetween("<input id=\"code\" value=\"", "\" type=\"hidden\">")
            )
        }

        val bitmap = Base64.decode(payData.second, Base64.DEFAULT).let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
        return Triple(payData.first, bitmap, payData.third)
    }
}
