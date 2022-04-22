package com.qhy040404.libraryonetap

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebView

class WebviewActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val sharedPreferences: SharedPreferences = getSharedPreferences("com.qhy040404.libraryonetap_preferences", Context.MODE_PRIVATE)

        var id :String = sharedPreferences.getString("userid", "Error").toString()
        var passwd :String = sharedPreferences.getString("passwd", "Error").toString()

        val libraryWebView: WebView = findViewById(R.id.webview)
        val cookieManager: CookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(libraryWebView, true)
        libraryWebView.settings.javaScriptEnabled = true
        libraryWebView.loadUrl("https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php")
    }
}