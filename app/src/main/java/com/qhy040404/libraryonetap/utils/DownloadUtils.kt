package com.qhy040404.libraryonetap.utils

import okhttp3.*
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

object DownloadUtils {
    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val response = chain.proceed(chain.request())
                response.newBuilder()
                    .body(ProgressResponseBody(response.body!!))
                    .build()
            }).build()
    }
    private const val GH_PROXY = "https://ghproxy.com/"
    private var onDownloadListener: WeakReference<OnDownloadListener>? = null

    /**
     * @param url      Download URL
     * @param file     File
     * @param listener Download callback
     */
    fun download(
        url: String,
        file: File,
        listener: OnDownloadListener?,
        github: Boolean = false,
    ) {
        onDownloadListener = WeakReference(listener)
        val request: Request = Request.Builder()
            .url(if (github) {
                GH_PROXY + url
            } else {
                url
            })
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onDownloadListener?.get()?.onDownloadFailed()
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (file.exists()) {
                    file.delete()
                }
                file.createNewFile()
                runCatching {
                    response.body?.let { body ->
                        body.byteStream().source().buffer().use { source ->
                            file.sink().buffer().use { output ->
                                output.writeAll(source)
                                onDownloadListener?.get()?.onDownloadSuccess()
                            }
                        }
                    } ?: run {
                        onDownloadListener?.get()?.onDownloadFailed()
                    }
                }.onFailure {
                    onDownloadListener?.get()?.onDownloadFailed()
                }
            }
        })
    }

    interface OnDownloadListener {
        fun onDownloadSuccess()
        fun onDownloading(progress: Int, done: Boolean)
        fun onDownloadFailed()
    }

    class ProgressResponseBody(private val responseBody: ResponseBody) : ResponseBody() {
        private var bufferedSource: BufferedSource? = null

        override fun contentType(): MediaType? {
            return responseBody.contentType()
        }

        override fun contentLength(): Long {
            return responseBody.contentLength()
        }

        override fun source(): BufferedSource {
            if (bufferedSource == null) {
                bufferedSource = mSource(responseBody.source()).buffer()
            }
            return bufferedSource!!
        }

        private fun mSource(source: Source): Source {
            return object : ForwardingSource(source) {
                var totalBytesRead = 0L
                override fun read(sink: Buffer, byteCount: Long): Long {
                    val bytesRead = super.read(sink, byteCount)
                    totalBytesRead += if (bytesRead != -1L) {
                        bytesRead
                    } else {
                        0
                    }
                    onDownloadListener?.get()
                        ?.onDownloading((totalBytesRead * 100 / responseBody.contentLength()).toInt(),
                            bytesRead == -1L)
                    return bytesRead
                }
            }
        }
    }
}
