package com.qhy040404.libraryonetap.utils

import android.content.Context
import com.qhy040404.libraryonetap.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException

object DownloadUtils {
    private val client by lazy { OkHttpClient() }
    private const val GH_PROXY = "https://ghproxy.com/"

    /**
     * @param url      Download URL
     * @param file     File
     * @param listener Download callback
     */
    fun download(
        ctx: Context,
        url: String,
        file: File,
        listener: OnDownloadListener,
        github: Boolean,
    ) {
        val request: Request = Request.Builder()
            .url(if (github) GH_PROXY + url else url)
            .build()
        Toasty.showShort(ctx, R.string.download_start)
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                listener.onDownloadFailed()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (file.exists()) file.delete()
                file.createNewFile()
                runCatching {
                    response.body?.let { body ->
                        body.byteStream().source().buffer().use { source ->
                            file.sink().buffer().use { output ->
                                output.writeAll(source)
                                listener.onDownloadSuccess()
                            }
                        }
                    } ?: run {
                        listener.onDownloadFailed()
                    }
                }.onFailure {
                    listener.onDownloadFailed()
                }
            }
        })
    }

    interface OnDownloadListener {
        fun onDownloadSuccess()
        fun onDownloadFailed()
    }
}
