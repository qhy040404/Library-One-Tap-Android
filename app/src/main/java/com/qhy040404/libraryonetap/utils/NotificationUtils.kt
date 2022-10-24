package com.qhy040404.libraryonetap.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Process
import androidx.core.app.NotificationCompat
import com.qhy040404.libraryonetap.R

class NotificationUtils(
    private val ctx: Context,
    private val channelId: String,
    private val channelName: String,
) {
    private val notificationId = Process.myPid()
    private val notificationIdNext = notificationId + 1
    private val notificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val builder = NotificationCompat.Builder(ctx, channelId)
        .setContentTitle(AppUtils.getResString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_about_foreground)
        .setLargeIcon(BitmapFactory.decodeResource(ctx.resources, R.mipmap.ic_launcher))
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setProgress(0, 0, true)
        .setSilent(true)
        .setOngoing(false)
        .setAutoCancel(false).apply {
            if (!OsUtils.atLeastS()) {
                color = ctx.getColor(R.color.colorPrimary)
            }
        }
    private val builderFinished = NotificationCompat.Builder(ctx, channelId)
        .setContentTitle(AppUtils.getResString(R.string.app_name))
        .setSmallIcon(R.drawable.ic_about_foreground)
        .setLargeIcon(BitmapFactory.decodeResource(ctx.resources, R.mipmap.ic_launcher))
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setSilent(true)
        .setOngoing(false)
        .setAutoCancel(false).apply {
            if (!OsUtils.atLeastS()) {
                color = ctx.getColor(R.color.colorPrimary)
            }
        }

    fun showNotification(content: String, progressBar: Boolean) {
        notificationManager.cancelAll()
        builder.setContentText(content).apply {
            if (progressBar) {
                setProgress(100, 0, false)
            }
        }
        notificationManager.apply {
            val name = channelName
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance)
            createNotificationChannel(channel)
        }
        notificationManager.notify(notificationId, builder.build())
    }

    fun updateProgress(progress: Int) {
        builder.setProgress(100, progress, false)
        notificationManager.notify(notificationId, builder.build())
    }

    fun finishProgress(content: String, intent: Intent) {
        builderFinished.setContentText(content)
            .setContentIntent(PendingIntent.getActivity(ctx, 0, intent, 0))
        notificationManager.apply {
            cancelAll()
            notify(notificationIdNext, builderFinished.build())
        }
    }
}
