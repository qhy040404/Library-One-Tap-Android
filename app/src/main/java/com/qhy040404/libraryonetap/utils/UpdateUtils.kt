package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.Intent
import android.os.StrictMode
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.GithubAPIDTO
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast
import com.qhy040404.libraryonetap.utils.extensions.FileExtensions.sha512
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

    suspend fun checkUpdate(ctx: Context, fromSettings: Boolean = false) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        if (!AppUtils.hasNetwork()) {
            if (fromSettings) {
                ctx.showToast(R.string.glb_net_disconnected)
            }
            return
        }
        if (!checkConnection()) {
            if (fromSettings) {
                ctx.showToast(R.string.stp_failed_to_connect_github_api)
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
            ctx.showToast(R.string.glb_net_timeout)
            return
        } catch (_: Exception) {
            ctx.showToast(R.string.stp_failed_to_connect_github_api)
            return
        }

        if (latestOrig.contains("API rate limit")) {
            if (fromSettings) {
                ctx.showToast(R.string.stp_github_api_rate_limit)
            }
            return
        }

        val latestClazz = latestOrig.decode<GithubAPIDTO>()!!

        val remoteVersionCode = getVersionCode(latestClazz.tag_name, false)
        val localVersionCode = getVersionCode(BuildConfig.VERSION_NAME, false)

        if (remoteVersionCode <= localVersionCode) {
            if (fromSettings) {
                ctx.showToast(R.string.stp_current_is_latest_version)
            }
            return
        }

        val validateName = latestClazz.assets[1].name
        val validateUrl = latestClazz.assets[1].browser_download_url
        val validateFile = File(ctx.cacheDir, validateName)
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
            latestClazz.body.substringBetween("Changelog", "---", reverse = true).trim().split("\n")

        GlobalValues.newVersion = versionName

        val dialogBody = buildString {
            append("<h2>")
            append(versionName)
            append("</h2>")
            changelog.forEach {
                if (it.isNotEmpty()) {
                    append("\t")
                    if (it.startsWith("* ")) {
                        append(it.substring(2))
                    } else if (it.startsWith("> ")) {
                        append("// ")
                        it.substring(2).let { content ->
                            if (content.surroundingWith("_")) {
                                append("<i>")
                                append(content.removeSurrounding("_"))
                                append("</i>")
                            } else if (content.surroundingWith("**")) {
                                append("<b>")
                                append(content.removeSurrounding("**"))
                                append("</b>")
                            } else {
                                append(content)
                            }
                        }
                    }
                    if (it != changelog.last()) {
                        append("<br>")
                    }
                }
            }
        }

        withContext(Dispatchers.Main) {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.upd_detected)
                .setMessage(HtmlCompat.fromHtml(
                    dialogBody,
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ))
                .setPositiveButton(R.string.upd_confirm) { _, _ ->
                    val validation = validateFile.readText().substringBefore("Library").trim()
                    File(ctx.cacheDir, packageName).let {
                        if (it.exists() && it.sha512() == validation) {
                            installApk(ctx, packageName)
                            return@setPositiveButton
                        }
                    }
                    val notification = NotificationUtils(ctx, "update", "Update")
                    ctx.showToast(R.string.glb_download_start)
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
                            File(ctx.cacheDir, packageName),
                            object : DownloadUtils.OnDownloadListener {
                                override fun onDownloadFailed() {
                                    ctx.showToast(R.string.glb_download_failed)
                                }

                                override fun onDownloadSuccess() {
                                    notification.finishProgress(R.string.glb_downloaded.getString())
                                    GlobalValues.latestApkName = packageName
                                    File(ctx.dataDir, Constants.CHANGELOG_INACTIVE).apply {
                                        if (exists()) {
                                            delete()
                                        }
                                        createNewFile()
                                        writeText(dialogBody)
                                    }
                                    installApk(ctx, packageName)
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

    private fun installApk(ctx: Context, name: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(
            FileProvider.getUriForFile(ctx,
                ctx.packageName + ".fileprovider",
                File(ctx.cacheDir, name)),
            "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        ctx.startActivity(intent)
    }

    private fun checkConnection(): Boolean {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )
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
