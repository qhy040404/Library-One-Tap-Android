package com.qhy040404.libraryonetap.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Process
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.absinthe.libraries.utils.extensions.activity
import com.qhy040404.libraryonetap.R
import com.qhy040404.libraryonetap.base.BaseActivity
import com.qhy040404.libraryonetap.utils.extensions.getString

class NotificationUtils(
    private val context: Context,
    private val channelId: String,
    private val channelName: String,
) {
    private var notificationPermission: Boolean = false
    private val notificationId = Process.myPid()
    private val notificationIdNext = notificationId + 1
    private val notificationManager = context.getSystemService<NotificationManager>()
    private val builder = initBuilder().setProgress(0, 0, true)
    private val builderFinished = initBuilder()

    init {
        notificationPermission = checkPermission()
    }

    fun showNotification(content: String, progressBar: Boolean) {
        if (!notificationPermission) {
            return
        }
        notificationManager?.cancelAll()
        builder.setContentText(content).apply {
            if (progressBar) {
                setProgress(100, 0, false)
            }
        }
        notificationManager?.apply {
            val name = channelName
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            createNotificationChannel(channel)
        }
        notificationManager?.notify(notificationId, builder.build())
    }

    fun updateProgress(progress: Int) {
        if (!notificationPermission) {
            return
        }
        builder.setProgress(100, progress, false)
        notificationManager?.notify(notificationId, builder.build())
    }

    fun finishProgress(content: String) {
        if (!notificationPermission) {
            return
        }
        builderFinished.setContentText(content)
        notificationManager?.apply {
            cancelAll()
            notify(notificationIdNext, builderFinished.build())
        }
    }

    private fun initBuilder() = NotificationCompat.Builder(context, channelId)
        .setContentTitle(R.string.app_name.getString())
        .setSmallIcon(R.drawable.ic_about_foreground)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSilent(true)
        .setOngoing(false)
        .setAutoCancel(false).apply {
            if (!OsUtils.atLeastS()) {
                color = context.getColor(R.color.colorPrimary)
            }
        }

    private fun checkPermission(): Boolean {
        return if (OsUtils.atLeastT()) {
            context.activity?.let {
                it as BaseActivity<*>
                PermissionUtils.checkPermission(
                    it,
                    Manifest.permission.POST_NOTIFICATIONS,
                    it.supportFragmentManager,
                    R.string.stp_notification_permission_prompt
                )
            } ?: false
        } else {
            true
        }
    }
}
