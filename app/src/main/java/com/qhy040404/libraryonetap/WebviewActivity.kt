package com.qhy040404.libraryonetap

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.StrictMode
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.qhy040404.libraryonetap.web.Requests


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

        val libraryWebView: WebView = findViewById(R.id.webview)
        val textView : TextView = findViewById(R.id.textView)
        val cookieManager: CookieManager = CookieManager.getInstance()
        val requests : Requests = Requests()

        cookieManager.setAcceptCookie(true)
        cookieManager.setAcceptThirdPartyCookies(libraryWebView, true)
        libraryWebView.settings.javaScriptEnabled = true
        libraryWebView.settings.javaScriptCanOpenWindowsAutomatically = true

        val sharedPreferences: SharedPreferences = getSharedPreferences("com.qhy040404.libraryonetap_preferences", Context.MODE_PRIVATE)

        val id :String = sharedPreferences.getString("userid", "Error").toString()
        val passwd :String = sharedPreferences.getString("passwd", "Error").toString()

        val ltResponse :String = requests.get("https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php")
        val ltData :String = "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"

        val rawData : String = "'$id$passwd$ltData'"

        libraryWebView.loadUrl("file:///android_assets/des.html")
        libraryWebView.webViewClient = object  : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                libraryWebView.evaluateJavascript("javascript:strEnc($rawData,'1','2','3')", object : ValueCallback<String> {
                    override fun onReceiveValue(p0: String?) {
                        val encryptedData : String? = p0
                    }
                })
            }
        }

        //Temp using
        libraryWebView.loadUrl("https://sso.dlut.edu.cn/cas/login?service=http://seat.lib.dlut.edu.cn/yanxiujian/client/login.php?redirect=index.php")
    }
}