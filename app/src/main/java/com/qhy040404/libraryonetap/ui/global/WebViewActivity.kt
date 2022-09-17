package com.qhy040404.libraryonetap.ui.global

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.databinding.ActivityWebviewBinding
import com.qhy040404.libraryonetap.utils.web.CookieJarImpl
import org.apache.http.util.EncodingUtils

class WebViewActivity : BaseActivity<ActivityWebviewBinding>() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun init() {
        val url = intent.extras?.getString("url")!!
        val body = intent.extras?.getString("body")

        setSupportActionBar(binding.toolbar)
        (binding.root as ViewGroup).bringChildToFront(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (!GlobalValues.md3) {
            binding.toolbar.setTitleTextColor(getColor(R.color.white))
            supportActionBar?.setHomeAsUpIndicator(R.drawable.white_back_btn)
        }

        binding.webview.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?,
            ): WebResourceResponse? {
                setCookie(request?.url.toString())
                return super.shouldInterceptRequest(view, request)
            }
        }

        val webSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true

        if (body == null) {
            binding.webview.loadUrl(url)
        } else {
            binding.webview.postUrl(url, EncodingUtils.getBytes(body, "utf-8"))
        }
    }

    private fun setCookie(url: String) {
        CookieManager.allowFileSchemeCookies()
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        CookieJarImpl.getCookies().forEach {
            val cookie = it.name + "=" + it.value + ";domain=" + it.domain + ";path=/"
            cookieManager.setCookie(url, cookie)
        }
        cookieManager.flush()
    }

    @Suppress("unused") // Remove when use open()
    companion object {
        fun open(ctx: Context, url: String, body: String? = null) {
            val intent = Intent(ctx, WebViewActivity::class.java)
            intent.putExtras(Bundle().apply {
                putString("url", url)
                if (body != null) {
                    putString("body", body)
                }
            })
            ctx.startActivity(intent)
        }
    }
}
