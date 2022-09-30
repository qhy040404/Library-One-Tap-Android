package com.qhy040404.libraryonetap.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
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
    private val notificationManager =
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val builder = NotificationCompat.Builder(ctx, channelId)
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

    fun finishProgress(content: String) {
        builder.setProgress(0, 0, false)
            .setContentText(content)
        notificationManager.notify(notificationId, builder.build())
    }
}
