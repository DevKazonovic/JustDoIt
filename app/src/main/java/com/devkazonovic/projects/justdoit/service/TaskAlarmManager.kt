package com.devkazonovic.projects.justdoit.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_NO_CREATE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.help.MyIntent.ACTION_SET_EXACT
import com.devkazonovic.projects.justdoit.help.MyIntent.EXTRA_EXACT_ALARM_DETAIL
import com.devkazonovic.projects.justdoit.help.MyIntent.EXTRA_EXACT_ALARM_ID
import com.devkazonovic.projects.justdoit.help.MyIntent.EXTRA_EXACT_ALARM_REQUEST_CODE
import com.devkazonovic.projects.justdoit.help.MyIntent.EXTRA_EXACT_ALARM_TIME
import com.devkazonovic.projects.justdoit.help.MyIntent.EXTRA_EXACT_ALARM_TITLE
import com.devkazonovic.projects.justdoit.receiver.DueDateBroadcastReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val timeHelper: DateTimeHelper,
) {

    private val intentForExactAlarm = Intent(
        context,
        DueDateBroadcastReceiver::class.java
    ).apply {
        action = ACTION_SET_EXACT
    }

    fun setDueDateAlarm(timeInMillis: Long, task: Task) {
        val time = timeHelper.getTimeInHourMinute(timeInMillis)
        if (timeHelper.isAfterNow(timeInMillis)) {
            val pendingRequestCode = task.alarmId!!
            if (isAlarmSet(pendingRequestCode)) {
                cancelAlarm(pendingRequestCode)
            }
            setAlarm(
                timeInMillis,
                getPendingIntent(intentForExactAlarm.apply {
                    putExtra(EXTRA_EXACT_ALARM_TIME, timeHelper.showTime(time.first, time.second))
                    putExtra(EXTRA_EXACT_ALARM_TITLE, task.title)
                    putExtra(EXTRA_EXACT_ALARM_DETAIL, task.detail)
                    putExtra(EXTRA_EXACT_ALARM_REQUEST_CODE, task.alarmId)
                    putExtra(EXTRA_EXACT_ALARM_ID, task.id)
                }, pendingRequestCode)
            )
        }

    }

    fun cancelDueDateAlarm(alarmId: Int) {
        if (isAlarmSet(alarmId)) {
            cancelAlarm(alarmId)
        }
    }

    private fun getPendingIntent(intent: Intent, requestCode: Int) =
        PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            FLAG_UPDATE_CURRENT
        )

    private fun setAlarm(timeInMillis: Long, pendingIntent: PendingIntent) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            timeInMillis, pendingIntent
        )
    }

    private fun isAlarmSet(requestCode: Int): Boolean {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intentForExactAlarm,
            FLAG_NO_CREATE
        )
        return pendingIntent != null
    }

    private fun cancelAlarm(requestCode: Int) {
        val oldPending = getPendingIntent(intentForExactAlarm, requestCode)
        alarmManager.cancel(oldPending)
    }


}


