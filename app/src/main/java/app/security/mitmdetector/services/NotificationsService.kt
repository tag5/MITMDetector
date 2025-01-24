package app.security.mitmdetector.services

import javax.inject.Singleton
import javax.inject.Inject

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.app.NotificationManager
import android.app.NotificationChannel
import androidx.core.app.NotificationCompat
import android.app.Notification
import app.security.mitmdetector.R

@Singleton
class NotificationsService @Inject constructor(@ApplicationContext private val context: Context) {
    private var notificationManager: NotificationManager
    private var channelId = "default_channel"

    init {
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelName = "Default Notifications"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance)
        notificationManager.createNotificationChannel(channel)
    }

    fun sendNotification(msg: String) {
        val notification = NotificationCompat.Builder(this.context, channelId)
            .setContentTitle("Security warning")
            .setContentText(msg)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .build()
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notification)
    }
}
