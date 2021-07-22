package com.devkazonovic.projects.mytasks.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat.*
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_NOTIFICATION_DISMISS
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.receiver.NotificationDismissedReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_TASK_ID = "Task ID"
private const val KEY_NOTIFICATION_ID = "Notification ID"

private const val CHANNEL_ID = "Reminder Channel Id"
private const val CHANNEL_NAME = "Reminder Channel"
private const val CHANNEL_DETAIL = "Reminder Channel For Tasks Reminders"

@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
) {

    val notifications = mutableMapOf<Int, Boolean>()

    fun createAlarmNotification(
        title: String,
        detail: String,
        taskID: Long,
        notificationID: Int
    ): Notification {
        return Builder(context, CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_round_timer)
            .setContentTitle(title)
            .setContentText(detail)
            .setPriority(PRIORITY_HIGH)
            .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(dismissedPendingIntent(notificationID))
            .setContentIntent(detailTaskDeepLink(taskID, notificationID).createPendingIntent())
            .build()
    }

    fun notify(notificationID: Int, notification: Notification) {
        with(notificationManager) {
            notify(notificationID, notification)
            notifications.put(notificationID, true)
        }
    }


    fun update(notificationID: Int, title: String, detail: String, taskID: Long) {
        val notification = createAlarmNotification(title, detail, taskID, notificationID)
        notify(notificationID, notification)
    }

    fun isNotificationVisible(notificationID: Int): Boolean {
        return notifications[notificationID] ?: false
    }

    fun cancel(notificationID: Int) {
        notificationManager.cancel(notificationID)
        notifications[notificationID] = false

    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DETAIL
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC;

            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun detailTaskDeepLink(taskID: Long, notificationID: Int): NavDeepLinkBuilder {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.main_graph)
            .setDestination(R.id.taskDetail)
            .setArguments(
                bundleOf(
                    KEY_TASK_ID to if (taskID == -1L) null else taskID,
                    KEY_NOTIFICATION_ID to notificationID
                )
            )
    }

    private fun dismissedPendingIntent(notificationID: Int): PendingIntent {
        log("${notificationID}")
        val intent = Intent(context, NotificationDismissedReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_DISMISS
        }.putExtra(EXTRA_NOTIFICATION_ID, notificationID)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        return pendingIntent
    }
}