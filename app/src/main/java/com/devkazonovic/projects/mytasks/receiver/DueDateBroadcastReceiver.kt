package com.devkazonovic.projects.mytasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_SET_EXACT
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_DETAIL
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_ID
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_REQUEST_CODE
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_TIME
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_TITLE
import com.devkazonovic.projects.mytasks.service.TaskAlarmManager
import com.devkazonovic.projects.mytasks.service.TaskNotificationManager
import com.devkazonovic.projects.mytasks.service.TaskRepeatAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DueDateBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var taskNotificationManager: TaskNotificationManager

    @Inject
    lateinit var taskAlarmManager: TaskAlarmManager

    @Inject
    lateinit var taskRepeatAlarmManager: TaskRepeatAlarmManager

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_SET_EXACT -> {
                val alarmID = intent.getIntExtra(EXTRA_EXACT_ALARM_REQUEST_CODE, -1)
                val taskID = intent.getLongExtra(EXTRA_EXACT_ALARM_ID, -1L)

                val taskTitle = intent.getStringExtra(EXTRA_EXACT_ALARM_TITLE)
                val taskDetail = intent.getStringExtra(EXTRA_EXACT_ALARM_DETAIL)
                val taskTime = intent.getStringExtra(EXTRA_EXACT_ALARM_TIME)

                buildNotification(
                    alarmID, taskID, "$taskTitle", "$taskDetail", "$taskTime"
                )
            }
        }
    }

    private fun buildNotification(
        alarmId: Int, taskID: Long, title: String, detail: String, time: String,
    ) {
        taskAlarmManager.cancelDueDateAlarm(alarmId)
        taskNotificationManager.showDueDateNotification(alarmId, taskID, title, detail, time)
        taskRepeatAlarmManager.resetAlarmIfRepeat(taskID)
    }
}