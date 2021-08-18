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
import com.devkazonovic.projects.mytasks.data.local.source.TaskNotificationDataSource
import com.devkazonovic.projects.mytasks.domain.IRxScheduler
import com.devkazonovic.projects.mytasks.domain.model.TaskNotification
import com.devkazonovic.projects.mytasks.domain.model.TaskNotificationState
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_NOTIFICATION_DISMISS
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.receiver.NotificationDismissedReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val KEY_TASK_ID = "Task ID"
private const val KEY_NOTIFICATION_ID = "Notification ID"
private const val CHANNEL_ID = "Reminder Channel Id"
private const val CHANNEL_NAME = "Reminder Channel"
private const val CHANNEL_DETAIL = "Reminder Channel For Tasks Reminders"

class TaskNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val taskNotificationDataSource: TaskNotificationDataSource,
    rxScheduler: IRxScheduler,
) {
    private val mainScheduler = rxScheduler.mainScheduler()
    private val ioScheduler = rxScheduler.ioScheduler()

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val descriptionText = CHANNEL_DETAIL
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(
                CHANNEL_ID, name, importance
            ).apply {
                description = descriptionText
                setShowBadge(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDueDateNotification(
        notificationID: Int, taskID: Long, title: String, detail: String, time: String,
    ) {
        val notification = createAlarmNotification(
            notificationID, taskID, title, detail, time
        )
        taskNotificationDataSource.insertNotification(
            TaskNotification(notificationID, TaskNotificationState.NOT_NOTIFY)
        ).subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { notify(notificationID, notification) },
                { log("DB Inserting Notification : ${it.message}") }
            )
    }

    fun updateDueDateNotification(
        notificationID: Int, taskID: Long, title: String, detail: String, time: String,
    ) {
        taskNotificationDataSource.findNotification(notificationID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe({
                if (it.state == TaskNotificationState.SHOWING) {
                    val notification =
                        createAlarmNotification(notificationID, taskID, title, detail, time)
                    notify(notificationID, notification)
                }
            },
                { log("DB findNotification : ${it.message}") }
            )


    }

    fun cancelDueDateNotification(
        notificationID: Int,
    ) {
        notificationManager.cancel(notificationID)
        deleteNotification(notificationID)
    }


    private fun createAlarmNotification(
        notificationID: Int, taskID: Long, title: String, detail: String, time: String,
    ): Notification {

        return Builder(context, CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_round_timer)
            .setContentTitle(title)
            .setContentText(time)
            .setPriority(PRIORITY_HIGH)
            .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(pendingIntentDismissed(notificationID))
            .setContentIntent(pendingIntentContent(taskID, notificationID).createPendingIntent())
            .build()

    }

    private fun notify(notificationID: Int, notification: Notification) {
        taskNotificationDataSource.updateNotification(
            TaskNotification(notificationID, TaskNotificationState.SHOWING)
        ).subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { with(notificationManager) { notify(notificationID, notification) } },
                { log("DB Updating Notification : ${it.message}") }
            )
    }

    private fun deleteNotification(notificationID: Int) {
        taskNotificationDataSource.deleteNotification(notificationID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe()
    }

    private fun pendingIntentContent(taskID: Long, notificationID: Int): NavDeepLinkBuilder {
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

    private fun pendingIntentDismissed(notificationID: Int): PendingIntent {
        val intent = Intent(context, NotificationDismissedReceiver::class.java).apply {
            action = ACTION_NOTIFICATION_DISMISS
        }.putExtra(EXTRA_NOTIFICATION_ID, notificationID)
        return PendingIntent.getBroadcast(
            context,
            notificationID,
            intent,
            PendingIntent.FLAG_ONE_SHOT
        )
    }

}