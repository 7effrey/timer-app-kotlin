package com.jeffrey.timer.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jeffrey.timer.MainActivity
import com.jeffrey.timer.R

class NotificationUtils {

    companion object {
        private const val NOTIFICATION_ID: Int = 123

        fun getNotificationManager(context: Context): NotificationManager {
            return (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        }

        fun updateNotification(context: Context, notificationTitle: String, notificationText: String): Pair<Int, Notification> {
            val notificationIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)

            val notificationChannelId = "Notification_Timer"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(notificationChannelId, "Notification", NotificationManager.IMPORTANCE_DEFAULT)
                notificationChannel.description = "TimerApp - Notification"
                notificationChannel.enableLights(false)
                notificationChannel.lightColor = Color.BLUE
                notificationChannel.enableVibration(false)
                notificationChannel.importance = NotificationManager.IMPORTANCE_MIN
                notificationChannel.setShowBadge(true)
                getNotificationManager(context).createNotificationChannel(notificationChannel)
            }

            val notification = NotificationCompat.Builder(context, notificationChannelId)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setOngoing(true)
                .build()
            return Pair(NOTIFICATION_ID, notification)
        }
    }
}