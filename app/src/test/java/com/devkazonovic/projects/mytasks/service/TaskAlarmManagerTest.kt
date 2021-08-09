package com.devkazonovic.projects.mytasks.service

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.receiver.DueDateBroadcastReceiver
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager
import org.robolectric.shadows.ShadowBroadcastReceiver
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId

@RunWith(RobolectricTestRunner::class)
@Config(maxSdk = Build.VERSION_CODES.P)
class TaskAlarmManagerTest {


    lateinit var context: Context
    lateinit var shadowAlarmManager: ShadowAlarmManager
    lateinit var alarmManager: AlarmManager
    lateinit var reminderManager: TaskAlarmManager
    lateinit var broadcastReceiver: DueDateBroadcastReceiver

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication().applicationContext
        broadcastReceiver = mock()
        alarmManager = context.getSystemService(AlarmManager::class.java)
        shadowAlarmManager = Shadows.shadowOf(alarmManager)
        val dateTimeHelper = DateTimeHelper(Clock.systemUTC())
        reminderManager = TaskAlarmManager(context, alarmManager, dateTimeHelper)
    }

    @Test
    fun testThatAlarmIfSet() {
        Truth.assertThat(shadowAlarmManager.nextScheduledAlarm).isNull()
        reminderManager.setDueDateAlarm(1626199903000, Task(title = "Title"))
        reminderManager.setDueDateAlarm(1626199904000, Task(title = "Title"))

        println(shadowAlarmManager.scheduledAlarms.size)
        Truth.assertThat(shadowAlarmManager.scheduledAlarms).hasSize(1)

    }

}