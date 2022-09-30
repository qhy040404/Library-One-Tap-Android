package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.Intent
import android.os.StrictMode
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalManager.moshi
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.model.GHAPIDataClass
import com.qhy040404.libraryonetap.utils.extensions.ContextExtension.showToast
import com.qhy040404.libraryonetap.utils.extensions.StringExtension.replaceAll
import com.qhy040404.libraryonetap.utils.web.Requests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import kotlin.concurrent.thread

object UpdateUtils {
    private val client = OkHttpClient()

    suspend fun checkUpdate(ctx: Context, fromSettings: Boolean = false) {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )
        val notification = NotificationUtils(ctx, "update", "Update")

        if (!checkConnection()) {
            if (fromSettings) {
                ctx.showToast(R.string.net_disconnected)
            }
            return
        }
        val request = Request.Builder()
            .url(URLManager.GITHUB_API_UPDATE)
            .addHeader("Accept", "application/vnd.github+json")
            .get()
            .build()
        val latestOrig = client.newCall(request).execute().body!!.string()
        val latestClazz = moshi.adapter(GHAPIDataClass::class.java).fromJson(latestOrig)!!

        val remoteVersionCode = latestClazz.tag_name.split(".").joinToString("")
        val localVersionCode = BuildConfig.VERSION_NAME.split(".").joinToString("")

        if (remoteVersionCode <= localVersionCode) {
            if (fromSettings) {
                ctx.showToast(R.string.current_is_latest_version)
            }
            return
        }

        val versionName = latestClazz.name
        val packageName = latestClazz.assets[0].name
        val packageUrl = latestClazz.assets[0].browser_download_url
        var changelog: List<String>
        latestClazz.body.split("---")[0].split("* ").apply {
            changelog = this.subList(1, this.size)
        }

        val dialogBody = StringBuilder()
        dialogBody.append("<h2>")
            .append(versionName)
            .append("</h2>")
        changelog.forEach {
            dialogBody.append(it.replaceAll("\n", "<br>"))
        }
        dialogBody.append(AppUtils.getResString(R.string.update_question))

        withContext(Dispatchers.Main) {
            MaterialAlertDialogBuilder(ctx)
                .setTitle(R.string.update_detected)
                .setMessage(HtmlCompat.fromHtml(
                    dialogBody.toString(),
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                ))
                .setPositiveButton(R.string.update_confirm) { _, _ ->
                    ctx.showToast(R.string.download_start)
                    notification.showNotification(versionName + AppUtils.getResString(R.string.downloading),
                        true)
                    thread {
                        StrictMode.setThreadPolicy(
                            StrictMode.ThreadPolicy.Builder().permitAll().build()
                        )
                        DownloadUtils.download(
                            ctx,
                            packageUrl,
                            File(ctx.cacheDir, packageName),
                            object : DownloadUtils.OnDownloadListener {
                                override fun onDownloadFailed() {
                                    ctx.showToast(R.string.download_failed)
                                }

                                override fun onDownloadSuccess() {
                                    notification.finishProgress(AppUtils.getResString(R.string.downloaded))
                                    installApk(ctx, packageName)
                                }

                                override fun onDownloading(progress: Int, done: Boolean) {
                                    notification.updateProgress(progress)
                                }
                            },
                            true
                        )
                    }
                }
                .setNegativeButton(R.string.update_dismiss, null)
                .setCancelable(true)
                .create().show()
        }
    }

    private fun installApk(ctx: Context, name: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setDataAndType(
            FileProvider.getUriForFile(ctx,
                "com.qhy040404.libraryonetap.fileprovider",
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
}
