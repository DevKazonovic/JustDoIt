package com.devkazonovic.projects.mytasks.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.devkazonovic.projects.mytasks.data.repository.TasksRepository
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.log
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import com.devkazonovic.projects.mytasks.service.TaskAlarmManager
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class BootBroadcastReceiver : BroadcastReceiver() {

    @Inject
    lateinit var tasksRepository: TasksRepository

    @Inject
    lateinit var reminderManager: TaskAlarmManager

    @Inject
    @Named(SCHEDULER_IO)
    lateinit var ioScheduler: Scheduler

    @Inject
    lateinit var dateTimeHelper: DateTimeHelper

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            resetAlarms()
        }
    }

    private fun resetAlarms() {
        tasksRepository
            .getAllTasks()
            .subscribeOn(ioScheduler)
            .subscribe { result ->
                when (result) {
                    is Result.Success -> {
                        log("${result.value}")
                        result.value.forEach { task ->
                            task.dueDate?.let { firstDueDate ->
                                if (dateTimeHelper.isAfterNow(firstDueDate))
                                    reminderManager.setDueDateAlarm(firstDueDate, task)
                                else {
                                    task.nextDueDate?.let { nextDueDate ->
                                        reminderManager.setDueDateAlarm(nextDueDate, task)
                                    }
                                }
                            }
                        }
                    }
                    is Result.Failure -> {
                        log("${result.throwable.message}")

                    }
                }
            }
    }
}