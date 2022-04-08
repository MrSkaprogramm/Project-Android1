package com.mitra

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.mitra.ui.main.MainActivity


class NotificationController {

    companion object {
        private const val GROUP_KEY_WORK_EMAIL = "com.mitra.WORK_CHAT"
        private var count = 0

        fun showNotification(
            title: String?,
            message: String?,
            context: Context,
            needSound: Boolean = false
        ) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val mBuilder: NotificationCompat.Builder =
                NotificationCompat
                    .Builder(context, NotificationChannelPreferences(context).getChannelId())
                    .setSmallIcon(R.drawable.ic_notification) // notification icon
                    .setColor(ContextCompat.getColor(context, R.color.notification))
                    .setContentTitle(title) // title for notification
                    .setContentText(message) // message for notification
                    .setGroup(GROUP_KEY_WORK_EMAIL)


            if (needSound) {
                mBuilder.setSound(alarmSound)
            }

            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE or Notification.DEFAULT_SOUND)
            mBuilder.setVibrate(longArrayOf(100L, 200L, 300L))

            if (count == 0) {
                mBuilder.setGroupSummary(true)
            }

            val intent = Intent(context, MainActivity::class.java)
            val pi = PendingIntent
                .getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            mBuilder.setContentIntent(pi)
            val notification = mBuilder.build()
            mNotificationManager?.notify(count, notification)
            count++
        }

        fun createNewNotificationChannel(context: Context) {
            val mNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelPreference = NotificationChannelPreferences(context)
                val channelId = channelPreference.getChannelId()
                val settings = SettingsPreferences(context)
                val vibrate = settings.getVibration()
                val sound = settings.getSound()
                val numberChannel = if (channelId.isNotEmpty()) {
                    mNotificationManager?.deleteNotificationChannel(channelId)
                    channelId
                        .replace("YOUR_CHANNEL_ID", "")
                        .toInt()
                } else {
                    0
                }
                val newChannelId = "YOUR_CHANNEL_ID" + (numberChannel + 1)
                NotificationChannelPreferences(context).setChannelId(newChannelId)
                val importance = if (sound)
                    NotificationManager.IMPORTANCE_DEFAULT
                else
                    NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(
                    newChannelId,
                    "Chat",
                    importance
                )
                channel.description = ""

                if (vibrate) {
                    channel.enableVibration(true)
                    channel.vibrationPattern = longArrayOf(100L, 200L, 300L, 400L, 1000L)
                } else {
                    channel.enableVibration(false)
                    channel.vibrationPattern = longArrayOf(0L)
                }

                mNotificationManager?.createNotificationChannel(channel)
            }
        }
    }
}