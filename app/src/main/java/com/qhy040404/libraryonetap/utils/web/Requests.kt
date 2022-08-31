package com.qhy040404.libraryonetap.utils.web

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.data.SessionData
import com.qhy040404.libraryonetap.utils.AppUtils
import com.qhy040404.libraryonetap.utils.lazy.resettableLazy
import com.qhy040404.libraryonetap.utils.lazy.resettableManager
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@Suppress("SpellCheckingInspection")
object Requests {
    val netLazyMgr = resettableManager()
    val client by resettableLazy(netLazyMgr) {
        OkHttpClient.Builder()
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .cookieJar(CookieJarImpl)
            .build()
    }

    fun get(url: String, textView: TextView? = null) = get(url, textView, false)

    fun get(url: String, textView: TextView? = null, getUrl: Boolean): String {
        if (!AppUtils.hasNetwork()) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_disconnected) }
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (getUrl) return response.request.url.toString()
                return response.body!!.string()
            }
        } catch (socket: SocketTimeoutException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_timeout) }
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (host: UnknownHostException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_error) }
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun getVCard(url: String): String {
        if (!AppUtils.hasNetwork()) return Constants.NET_DISCONNECTED
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .get()
            .build()
        try {
            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        } catch (socket: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (host: UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun post(url: String, form: String, FORM: MediaType, textView: TextView? = null) =
        post(url, form, FORM, textView, false)

    fun post(
        url: String,
        form: String,
        FORM: MediaType,
        textView: TextView? = null,
        getUrl: Boolean,
    ): String {
        if (!AppUtils.hasNetwork()) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_disconnected) }
            GlobalValues.netError = true
            return Constants.NET_DISCONNECTED
        }
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (getUrl) return response.request.url.toString()
                return response.body!!.string()
            }
        } catch (socket: SocketTimeoutException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_timeout) }
            GlobalValues.netError = true
            return Constants.NET_TIMEOUT
        } catch (host: UnknownHostException) {
            textView?.post { textView.text = AppUtils.getResString(R.string.net_error) }
            GlobalValues.netError = true
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun postVCard(url: String, form: String, FORM: MediaType): String {
        if (!AppUtils.hasNetwork()) return Constants.NET_DISCONNECTED
        val body = form.toRequestBody(FORM)
        val request = Request.Builder()
            .url(url)
            .removeHeader("User-Agent")
            .addHeader("User-Agent", "weishao")
            .post(body)
            .build()
        try {
            client.newCall(request).execute()
                .use { response -> return response.body!!.string() }
        } catch (socket: SocketTimeoutException) {
            return Constants.NET_TIMEOUT
        } catch (host: UnknownHostException) {
            return Constants.NET_ERROR
        } catch (e: Exception) {
            return Constants.STRING_NULL
        }
    }

    fun loginSso(
        sso: String,
        mt: MediaType,
        session: String? = null,
        progressBar: ProgressBar? = null,
        needCheck: Boolean = false,
        noJsonString: String = "统一身份",
        hasSessionJson: Boolean = false,
    ): Boolean {
        val id = GlobalValues.id
        val passwd = GlobalValues.passwd

        var loginSuccess = false
        var timer = 0

        while (AppUtils.checkData(id, passwd)) {
            val ltResponse = get(sso)
            val ltData = try {
                "LT" + ltResponse.split("LT")[1].split("cas")[0] + "cas"
            } catch (_: Exception) {
                ""
            }
            val ltExecution = try {
                ltResponse.split("name=\"execution\" value=\"")[1].split("\"")[0]
            } catch (_: Exception) {
                ""
            }

            if (ltData.isNotEmpty()) {
                val rawData = "$id$passwd$ltData"
                val rsa = GlobalManager.des.strEnc(rawData, "1", "2", "3")

                Thread.sleep(200L)

                post(
                    sso,
                    loginPostData(id, passwd, ltData, rsa, ltExecution),
                    mt
                )
            }

            if (needCheck) {
                if (hasSessionJson) {
                    val sessionCheck = post(session!!, "", mt)
                    if (SessionData.isSuccess(sessionCheck)) {
                        progressBar?.post { progressBar.visibility = View.INVISIBLE }
                        loginSuccess = true
                        break
                    } else timer++
                } else {
                    val sessionCheck = get(session!!)
                    if (!sessionCheck.contains(noJsonString)) {
                        loginSuccess = true
                        break
                    } else timer++
                }
                if (timer == 2) {
                    netLazyMgr.reset()
                    CookieJarImpl.reset()
                }
                if (timer >= 3) break
            }
        }
        return loginSuccess
    }

    fun loginPostData(
        id: String,
        passwd: String,
        ltData: String,
        rsa: String,
        execution: String,
    ) =
        "none=on&rsa=$rsa&ul=${id.length}&pl=${passwd.length}&sl=0&lt=$ltData&execution=$execution&_eventId=submit"
}
