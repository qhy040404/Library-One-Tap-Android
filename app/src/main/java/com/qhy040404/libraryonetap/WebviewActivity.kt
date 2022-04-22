package com.qhy040404.libraryonetap

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.Requests


class WebviewActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
            .detectDiskReads().detectDiskWrites().detectNetwork()
            .penaltyLog().build())
        StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder()
            .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
            .penaltyLog().penaltyDeath().build())

        val sharedPreferences: SharedPreferences = getSharedPreferences("com.qhy040404.libraryonetap_preferences", Context.MODE_PRIVATE)

        var id :String = sharedPreferences.getString("userid", "Error").toString()
        var passwd :String = sharedPreferences.getString("passwd", "Error").toString()

        val libraryWebView: WebView = findViewById(R.id.webview)
        val cookieManager: CookieManager = CookieManager.getInstance()

        val requests :Requests = Requests()
        val ltResponse :String = requests.run("https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php")
        val ltData :String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(libraryWebView, true)
        libraryWebView.settings.javaScriptEnabled = true
        //Temp using
        libraryWebView.loadUrl("https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php")
    }
}