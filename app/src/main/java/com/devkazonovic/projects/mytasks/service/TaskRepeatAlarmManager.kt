package com.devkazonovic.projects.mytasks.service

import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.IRxScheduler
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.model.RepeatType
import com.devkazonovic.projects.mytasks.domain.model.Task
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TaskRepeatAlarmManager @Inject constructor(
    private val taskRepository: ITasksRepository,
    private val dateTimeHelper: DateTimeHelper,
    private val taskAlarmManager: TaskAlarmManager,
    rxScheduler: IRxScheduler,
) {
    private val mainScheduler = rxScheduler.mainScheduler()
    private val ioScheduler = rxScheduler.ioScheduler()

    fun resetAlarmIfRepeat(taskID: Long) {
        taskRepository.getTask(taskID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { data ->
                when (data) {
                    is Result.Success -> {
                        val task = data.value
                        task.nextDueDate?.let { nextDueDate ->
                            taskAlarmManager.setDueDateAlarm(nextDueDate, task)
                            task.repeatType?.let { repeatType ->
                                task.repeatValue?.let { repeatValue ->
                                    updateDueDate(task, addRepeatValue(
                                        repeatType,
                                        repeatValue,
                                        dateTimeHelper.fromLongToLocalDate(nextDueDate),
                                        dateTimeHelper.getTimeInHourMinute(nextDueDate)
                                    )
                                    )
                                }
                            }
                        }


                    }
                    is Result.Failure -> {

                    }
                }
            }
    }

    private fun updateDueDate(task: Task, newDueDate: Long) {
        taskRepository.updateTask(task.copy(nextDueDate = newDueDate))
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe()
    }

    fun addRepeatValue(
        repeatType: RepeatType, repeatValue: Int, date: LocalDate, time: Pair<Int, Int>,
    ): Long {
        return when (repeatType) {
            RepeatType.DAY -> {
                dateTimeHelper.groupDateTime(
                    date.plusDays(repeatValue.toLong()),
                    time.first,
                    time.second
                )
            }
            RepeatType.WEEK -> {
                dateTimeHelper.groupDateTime(
                    date.plusWeeks(repeatValue.toLong()),
                    time.first,
                    time.second
                )

            }
            RepeatType.MONTH -> {
                dateTimeHelper.groupDateTime(
                    date.plusMonths(repeatValue.toLong()),
                    time.first,
                    time.second
                )
            }
            RepeatType.YEAR -> {
                dateTimeHelper.groupDateTime(
                    date.plusYears(repeatValue.toLong()),
                    time.first,
                    time.second
                )
            }
        }
    }
}