package com.senla.fitnessapp.presentation.notification.broadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.senla.fitnessapp.R
import com.senla.fitnessapp.common.MainActivity
import com.senla.fitnessapp.presentation.notification.notificationDialog.NotificationDialogFragment.Companion.PENDING_INTENT_REQUEST_CODE

class NotificationReceiver : BroadcastReceiver() {

    companion object {
        const val JOGGING_FRAGMENT_EXTRA_KEY = "JoggingFragment"
        private const val NOTIFICATION_TITLE = "Your Health"
        private const val NOTIFICATION_TEXT = "Пожалуйста, начните тренировку!" +
                " В здоровом теле здоровый дух!"
        private const val CHANNEL_ID = "channelID"
        private const val CHANNEL_NAME = "channelName"
        private const val NOTIFICATION_ID = 0
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context)

        val pendingIntent = createNotificationIntents(context)

        createNotification(context, pendingIntent)
    }

    private fun createNotificationChannel(context: Context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH).apply {
                lightColor = Color.GREEN
                enableLights(true)
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun createNotificationIntents(context: Context): PendingIntent {
        val notificationIntent = Intent(context, MainActivity::class.java)
        notificationIntent.putExtra(JOGGING_FRAGMENT_EXTRA_KEY, true)
        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(notificationIntent)
            getPendingIntent(PENDING_INTENT_REQUEST_CODE,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        return pendingIntent
    }

    private fun createNotification(context: Context, pendingIntent: PendingIntent) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setSmallIcon(R.id.icon)
            .setContentText(NOTIFICATION_TEXT)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}