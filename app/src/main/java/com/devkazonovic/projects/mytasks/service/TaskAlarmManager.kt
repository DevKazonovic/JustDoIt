package com.devkazonovic.projects.mytasks.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_NO_CREATE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.MyIntent.ACTION_SET_EXACT
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_DETAIL
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_ID
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_REQUEST_CODE
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_TIME
import com.devkazonovic.projects.mytasks.help.MyIntent.EXTRA_EXACT_ALARM_TITLE
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.receiver.ReminderBroadcastReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class TaskAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmManager: AlarmManager,
    private val dateTimeHelper: DateTimeHelper,
) {

    private val intentForExactAlarm = Intent(context, ReminderBroadcastReceiver::class.java).apply {
        action = ACTION_SET_EXACT
    }

    fun setExactReminder(timeInMillis: Long, dataToShow: Task) {
        if (dateTimeHelper.isAfterNow(timeInMillis)) {
            val pendingRequestCode = dataToShow.pendingIntentRequestCode!!
            if (isAlarmSet(pendingRequestCode)) {
                cancelAlarm(pendingRequestCode)
            }
            setAlarm(
                timeInMillis,
                getPendingIntent(intentForExactAlarm.apply {
                    putExtra(EXTRA_EXACT_ALARM_TIME, timeInMillis)
                    putExtra(EXTRA_EXACT_ALARM_TITLE, dataToShow.title)
                    putExtra(EXTRA_EXACT_ALARM_DETAIL, dataToShow.detail)
                    putExtra(EXTRA_EXACT_ALARM_REQUEST_CODE, dataToShow.pendingIntentRequestCode)
                    putExtra(EXTRA_EXACT_ALARM_ID, dataToShow.id)
                }, pendingRequestCode)
            )
        }

    }

    fun cancelReminder(requestCode: Int) {
        if (isAlarmSet(requestCode)) {
            cancelAlarm(requestCode)
        }

        isAlarmSet(requestCode)
    }

    private fun getPendingIntent(intent: Intent, requestCode: Int) = PendingIntent.getBroadcast(
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
        log("isAlarmSet : ${pendingIntent != null}")
        return pendingIntent != null

    }

    private fun cancelAlarm(requestCode: Int) {
        val oldPending = getPendingIntent(
            intentForExactAlarm,
            requestCode
        )

        alarmManager.cancel(oldPending)

    }


}


