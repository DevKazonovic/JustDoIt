package com.devkazonovic.projects.mytasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_SET_EXACT
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_DETAIL
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_ID
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_REQUEST_CODE
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_TITLE
import com.devkazonovic.projects.mytasks.service.TaskAlarmManager
import com.devkazonovic.projects.mytasks.service.TaskNotificationManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskNotificationManager: TaskNotificationManager

    @Inject
    lateinit var taskAlarmManager: TaskAlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SET_EXACT -> {
                val taskTitle = intent.getStringExtra(EXTRA_EXACT_ALARM_TITLE)
                val taskDetail = intent.getStringExtra(EXTRA_EXACT_ALARM_DETAIL)
                val taskID = intent.getLongExtra(EXTRA_EXACT_ALARM_ID, -1L)
                val pendingRequestCode = intent.getIntExtra(EXTRA_EXACT_ALARM_REQUEST_CODE, -1)
                buildNotification("$taskTitle", "$taskDetail", taskID, pendingRequestCode)
            }
        }
    }

    private fun buildNotification(
        title: String,
        detail: String,
        taskID: Long,
        requestCode: Int
    ) {

        taskNotificationManager.showNotification(title, detail, taskID, requestCode)
        taskAlarmManager.cancelReminder(requestCode)

    }
}