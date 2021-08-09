package com.devkazonovic.projects.mytasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_NOTIFICATION_DISMISS
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_NOTIFICATION_ID
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.service.TaskAlarmManager
import com.devkazonovic.projects.mytasks.service.TaskNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDismissedReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskNotificationManager: TaskNotificationManager

    @Inject
    lateinit var taskAlarmManager: TaskAlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        log(intent.toString())
        if (intent.action == ACTION_NOTIFICATION_DISMISS) {
            val notificationID = intent.extras?.getInt(EXTRA_NOTIFICATION_ID)!!
            taskNotificationManager.cancelDueDateNotification(notificationID)
        }
    }
}