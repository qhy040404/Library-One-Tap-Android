package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.Intent
import android.os.StrictMode
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.qhy040404.libraryonetap.BuildConfig
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.constant.Constants
import com.qhy040404.libraryonetap.constant.GlobalValues
import com.qhy040404.libraryonetap.constant.OnceTag
import com.qhy040404.libraryonetap.constant.URLManager
import com.qhy040404.libraryonetap.data.GithubAPIDTO
import com.qhy040404.libraryonetap.utils.extensions.decode
import com.qhy040404.libraryonetap.utils.extensions.getString
import com.qhy040404.libraryonetap.utils.extensions.markdownToSpanned
import com.qhy040404.libraryonetap.utils.extensions.sha512
import com.qhy040404.libraryonetap.utils.extensions.showToast
import com.qhy040404.libraryonetap.utils.extensions.substringBetween
import com.qhy040404.libraryonetap.utils.web.Requests
import java.io.File
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import jonathanfinerty.once.Once
import kotlin.concurrent.thread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object UpdateUtils {
  private val client = OkHttpClient()
  private var dialog: WeakReference<AlertDialog>? = null

  suspend fun checkUpdate(context: Context, fromSettings: Boolean = false) {
    if (BuildConfig.DEBUG && !Once.beenDone(Once.THIS_APP_SESSION, OnceTag.APP_INITIALIZED)) {
      context.showToast(R.string.upd_debug_version)
      Once.markDone(OnceTag.APP_INITIALIZED)
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
    val changelog = latestClazz.body.substringBetween("Changelog", "---", reverse = true).trim()

    GlobalValues.newVersion = versionName
    GlobalValues.newVersionLiveData.postValue(versionName)

    withContext(Dispatchers.Main) {
      MaterialAlertDialogBuilder(context)
        .setTitle(R.string.upd_detected)
        .setMessage(changelog.markdownToSpanned())
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
                    writeText(changelog)
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
        str.apply {
          if (fromPackage) {
            substringBetween("_v", "-release")
          }
        }.split(".").forEach {
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
