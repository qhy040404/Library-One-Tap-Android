package com.qhy040404.libraryonetap.ui.global

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.databinding.ActivityWebviewBinding
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType

class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val url = intent.extras?.getString("url")!!
        val body = intent.extras?.getString("body")
        val mediaType = intent.extras?.getString("mediaType")?.toMediaType()

        setSupportActionBar(binding.toolbar)
        (binding.root as ViewGroup).bringChildToFront(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                lifecycleScope.launch { view?.loadRequest(request?.url.toString()) }
                return true
            }
        }

        val webSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true

        lifecycleScope.launch {
            if (body == null) {
                binding.webview.loadRequest(url)
            } else {
                binding.webview.postRequest(url, body, mediaType!!)
            }
        }
    }

    private suspend fun WebView.loadRequest(url: String) {
        val html = withContext(Dispatchers.IO) {
            Requests.get(url)
        }
        clearCache(true)
        loadDataWithBaseURL(url, html, "text/html", "utf-8", null)
    }

    private suspend fun WebView.postRequest(url: String, postData: String, mediaType: MediaType) {
        val html = withContext(Dispatchers.IO) {
            Requests.post(url, postData, mediaType)
        }
        clearCache(true)
        loadDataWithBaseURL(url, html, "text/html", "utf-8", null)
    }

    @Suppress("unused") // Remove when use open()
    companion object {
        fun open(ctx: Context, url: String, body: String? = null, mediaType: String? = null) {
            val intent = Intent(ctx, WebViewActivity::class.java)
            intent.putExtras(Bundle().apply {
                putString("url", url)
                if (body != null) {
                    putString("body", body)
                    if (mediaType == null) {
                        throw IllegalArgumentException("You must define mediaType when you defined body.")
                    }
                    putString("mediaType", mediaType)
                }
            })
            ctx.startActivity(intent)
        }
    }
}
