package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.StrictMode
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.core.text.toHtml
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.GithubAPIDTO
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast
import com.qhy040404.libraryonetap.utils.extensions.FileExtensions.sha512
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getColor
import com.qhy040404.libraryonetap.utils.extensions.IntExtensions.getString
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.decode
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.substringBetween
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.surroundingWith
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import kotlin.concurrent.thread

object UpdateUtils {
    private val client = OkHttpClient()
    private var dialog: WeakReference<AlertDialog>? = null

    suspend fun checkUpdate(context: Context, fromSettings: Boolean = false) {
        if (BuildConfig.DEBUG) {
            context.showToast(R.string.upd_debug_version)
        }
        if (!AppUtils.hasNetwork()) {
            if (fromSettings) {
                context.showToast(R.string.glb_net_disconnected)
            }
            return
        }
        if (!checkConnection()) {
            if (fromSettings) {
                context.showToast(R.string.stp_failed_to_connect_github_api)
            }
            return
        }
        val request = Request.Builder()
            .url(URLManager.GITHUB_API_UPDATE)
            .addHeader("Accept", "application/vnd.github+json")
            .get()
            .build()
        val latestOrig = try {
            client.newCall(request).execute().body!!.string()
        } catch (s: SocketTimeoutException) {
            context.showToast(R.string.glb_net_timeout)
            return
        } catch (_: Exception) {
            context.showToast(R.string.stp_failed_to_connect_github_api)
            return
        }

        if (latestOrig.contains("API rate limit")) {
            if (fromSettings) {
                context.showToast(R.string.stp_github_api_rate_limit)
            }
            return
        }

        val latestClazz = latestOrig.decode<GithubAPIDTO>()!!

        val remoteVersionCode = getVersionCode(latestClazz.tag_name, false)
        val localVersionCode = getVersionCode(BuildConfig.VERSION_NAME, false)

        if (remoteVersionCode <= localVersionCode) {
            if (fromSettings) {
                context.showToast(R.string.stp_current_is_latest_version)
            }
            return
        }

        val validateName = latestClazz.assets[1].name
        val validateUrl = latestClazz.assets[1].browser_download_url
        val validateFile = File(context.cacheDir, validateName)
        DownloadUtils.download(
            validateUrl,
            validateFile,
            listener = null,
            github = true,
            async = false
        )

        val versionName = latestClazz.name
        val packageName = latestClazz.assets[0].name
        val packageUrl = latestClazz.assets[0].browser_download_url
        val changelog: List<String> =
            latestClazz.body.substringBetween("Changelog", "---", reverse = true).trim()
                .split("\r\n")

        GlobalValues.newVersion = versionName

        val dialogBody = buildSpannedString {
            inSpans(
                RelativeSizeSpan(1.4F),
                StyleSpan(Typeface.BOLD)
            ) {
                append(versionName)
            }
            appendLine()
            appendLine()
            changelog.forEach {
                val str = it.trim()
                if (str.isNotEmpty()) {
                    append('\t')
                    if (str.startsWith("* ")) {
                        append(
                            str.substring(2)
                        )
                    } else {
                        append(
                            buildSpannedString {
                                var mStr = str
                                val spans = mutableListOf<Any>()
                                if (mStr.startsWith("> ")) {
                                    spans.add(
                                        ForegroundColorSpan(
                                            R.color.material_grey_500.getColor(
                                                context
                                            )
                                        )
                                    )
                                    mStr = mStr.substring(2)
                                }
                                while (mStr.surroundingWith("_") || mStr.surroundingWith("**")) {
                                    if (mStr.surroundingWith("_")) {
                                        spans.add(StyleSpan(Typeface.ITALIC))
                                        mStr = mStr.removeSurrounding("_")
                                    }
                                    if (mStr.surroundingWith("**")) {
                                        spans.add(StyleSpan(Typeface.BOLD))
                                        mStr = mStr.removeSurrounding("**")
                                    }
                                }
                                append("$mStr\n")
                                spans.forEach { span ->
                                    setSpan(
                                        span,
                                        0,
                                        mStr.length,
                                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                        )
                    }
                    if (it != changelog.last()) {
                        appendLine()
                    }
                }
            }
        }

        withContext(Dispatchers.Main) {
            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.upd_detected)
                .setMessage(dialogBody)
                .setPositiveButton(R.string.upd_confirm) { _, _ ->
                    val validation = validateFile.readText().substringBefore("Library").trim()
                    File(context.cacheDir, packageName).let {
                        if (it.exists() && it.sha512() == validation) {
                            installApk(context, packageName)
                            return@setPositiveButton
                        }
                    }
                    val notification = NotificationUtils(context, "update", "Update")
                    context.showToast(R.string.glb_download_start)
                    notification.showNotification(
                        "$versionName ${R.string.glb_downloading.getString()}",
                        true
                    )
                    thread {
                        StrictMode.setThreadPolicy(
                            StrictMode.ThreadPolicy.Builder().permitAll().build()
                        )
                        DownloadUtils.download(
                            packageUrl,
                            File(context.cacheDir, packageName),
                            object : DownloadUtils.OnDownloadListener {
                                override fun onDownloadFailed() {
                                    context.showToast(R.string.glb_download_failed)
                                }

                                override fun onDownloadSuccess() {
                                    notification.finishProgress(R.string.glb_downloaded.getString())
                                    GlobalValues.latestApkName = packageName
                                    File(context.dataDir, Constants.CHANGELOG_INACTIVE).apply {
                                        if (exists()) {
                                            delete()
                                        }
                                        createNewFile()
                                        writeText(dialogBody.toHtml())
                                    }
                                    installApk(context, packageName)
                                }

                                override fun onDownloading(progress: Int, done: Boolean) {
                                    notification.updateProgress(progress)
                                    super.onDownloading(progress, done)
                                }
                            },
                            github = true,
                            async = true
                        )
                    }
                }
                .setNegativeButton(R.string.upd_dismiss, null)
                .setCancelable(true)
                .setOnDismissListener {
                    if (dialog?.get() == it) {
                        dialog = null
                    }
                }
                .create().also {
                    dialog?.get()?.dismiss()
                    dialog = WeakReference(it)
                    it.show()
                }
        }
    }

    fun getVersionCode(str: String, fromPackage: Boolean): Int {
        return if (str.isEmpty()) {
            0
        } else {
            buildList {
                (if (fromPackage) {
                    str.substringBetween("_v", "-release")
                } else {
                    str
                }).split(".").forEach {
                    add(it.formatVersion())
                }
            }.joinToString("").toInt()
        }
    }

    private fun installApk(context: Context, name: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(
            FileProvider.getUriForFile(
                context,
                GlobalValues.FP_NAME,
                File(context.cacheDir, name)
            ),
            "application/vnd.android.package-archive"
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(intent)
    }

    private fun checkConnection(): Boolean {
        return when (Requests.get(URLManager.GITHUB_REPO)) {
            Constants.NET_TIMEOUT -> false
            Constants.NET_ERROR -> false
            Constants.STRING_NULL -> false
            else -> true
        }
    }

    private fun String.formatVersion(): String {
        return when (this.length) {
            1 -> "0$this"
            2 -> this
            else -> throw IllegalArgumentException("Illegal version")
        }
    }
}
