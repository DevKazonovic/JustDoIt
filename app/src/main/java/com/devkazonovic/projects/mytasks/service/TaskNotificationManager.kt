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
import com.devkazonovic.projects.mytasks.data.local.TaskNotificationDataSource
import com.devkazonovic.projects.mytasks.domain.model.TaskNotification
import com.devkazonovic.projects.mytasks.domain.model.TaskNotificationState
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_NOTIFICATION_DISMISS
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_MAIN
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.receiver.NotificationDismissedReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Named

private const val KEY_TASK_ID = "Task ID"
private const val KEY_NOTIFICATION_ID = "Notification ID"
private const val CHANNEL_ID = "Reminder Channel Id"
private const val CHANNEL_NAME = "Reminder Channel"
private const val CHANNEL_DETAIL = "Reminder Channel For Tasks Reminders"

class TaskNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationManager: NotificationManagerCompat,
    private val taskNotificationDataSource: TaskNotificationDataSource,
    @Named(SCHEDULER_MAIN) private val mainScheduler: Scheduler,
    @Named(SCHEDULER_IO) private val ioScheduler: Scheduler
) {
    fun createAlarmNotification(
        title: String,
        detail: String,
        taskID: Long,
        notificationID: Int
    ): Notification {

        val notification = Builder(context, CHANNEL_ID)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_round_timer)
            .setContentTitle(title)
            .setContentText(detail)
            .setPriority(PRIORITY_HIGH)
            .setDefaults(DEFAULT_SOUND or DEFAULT_VIBRATE)
            .setOnlyAlertOnce(true)
            .setDeleteIntent(pendingIntentDismissed(notificationID))
            .setContentIntent(pendingIntentContent(taskID, notificationID).createPendingIntent())
            .build()

        return notification
    }


    fun showNotification(title: String,
                         detail: String,
                         taskID: Long,
                         notificationID: Int){
        val notification = createAlarmNotification(
            title, detail, taskID, notificationID
        )
        taskNotificationDataSource.insertNotification(
            TaskNotification(notificationID, TaskNotificationState.NOT_NOTIFY)
        ).subscribeOn(ioScheduler).subscribeOn(mainScheduler).subscribe(
            {notify(notificationID, notification)},{}
        )
    }

    fun notify(notificationID: Int, notification: Notification) {
                taskNotificationDataSource.updateNotification(
                    TaskNotification(notificationID, TaskNotificationState.SHOWING)
                ).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe(
                    {
                        with(notificationManager) {
                            notify(notificationID, notification)
                        }
                    },
                    {}
                )
    }

    fun update(notificationID: Int, title: String, detail: String, taskID: Long) {
        taskNotificationDataSource.findNotification(notificationID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe({
                if (it.state == TaskNotificationState.SHOWING) {
                    val notification =
                        createAlarmNotification(title, detail, taskID, notificationID)
                    notify(notificationID, notification)
                }
            },
                { log("${it.message}") }
            )


    }

    fun cancel(notificationID: Int) {
        notificationManager.cancel(notificationID)
        deleteNotification(notificationID)
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

    private fun saveNotification(notificationID: Int) {

    }

    private fun updateNotification(notificationID: Int, state: TaskNotificationState) {
        taskNotificationDataSource.updateNotification(
            TaskNotification(notificationID, state)
        ).subscribeOn(ioScheduler).observeOn(mainScheduler).subscribe()
    }

    private fun deleteNotification(notificationID: Int) {
        taskNotificationDataSource.deleteNotification(notificationID)
            .subscribeOn(ioScheduler).subscribeOn(mainScheduler).subscribe()
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
        log("$notificationID")
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