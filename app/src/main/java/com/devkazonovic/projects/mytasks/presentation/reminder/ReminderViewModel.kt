package com.devkazonovic.projects.mytasks.presentation.reminder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.holder.Event
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_MAIN
import com.devkazonovic.projects.mytasks.help.util.handleResult
import com.devkazonovic.projects.mytasks.service.AlarmHelper
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import com.devkazonovic.projects.mytasks.service.NotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val dateTimeHelper: DateTimeHelper,
    private val reminderManager: AlarmHelper,
    private val tasksRepository: ITasksRepository,
    @Named(SCHEDULER_MAIN) private val mainScheduler: Scheduler,
    @Named(SCHEDULER_IO) private val ioScheduler: Scheduler
) : ViewModel() {

    private val _currentTask = MutableLiveData<Task>()
    private val _date = MutableLiveData<Long?>()
    private val _time = MutableLiveData<Pair<Int, Int>?>()
    private val _timeMillis = MutableLiveData<Long>()
    private val _isDateTimeSelected = MutableLiveData<Event<Boolean>>()

    val date: LiveData<Long?> get() = _date
    val time: LiveData<Pair<Int, Int>?> get() = _time

    val dateStr = Transformations.switchMap(_date) { dateInMillis ->
        dateInMillis?.let {
            MutableLiveData<String>().apply { this.value = dateTimeHelper.showDate(it) }
        } ?: MutableLiveData<String>().apply { this.value = null }
    }
    val timeStr = Transformations.switchMap(_time) { pair ->
        pair?.let {
            MutableLiveData<String>().apply {
                this.value = dateTimeHelper.showTime(it.first, it.second)
            }
        } ?: MutableLiveData<String>().apply { this.value = null }

    }
    val isTaskHasReminder = Transformations.switchMap(_currentTask) { currentTask ->
        MutableLiveData<Boolean>().apply { value = currentTask.reminderDate != null }
    }
    val timeMillis: LiveData<Long> get() = _timeMillis
    val isDateTimeSelected: LiveData<Event<Boolean>> get() = _isDateTimeSelected


    fun start(taskID: Long) {
        tasksRepository.getTask(taskID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(
                    result,
                    { task ->
                        _currentTask.postValue(task)
                        getCurrentTaskReminder(task.reminderDate)
                    },
                    { }
                )
            }
    }

    private fun getCurrentTaskReminder(dateTimeInMillis: Long?) {
        dateTimeInMillis?.let {
            _date.value = dateTimeHelper.getDateInMillis(dateTimeInMillis)
            _time.value = dateTimeHelper.getTimeInHourMinute(dateTimeInMillis)
        } ?: run {
            _date.value = null
            _time.value = null
        }
    }

    fun setDate(utc: Long?) {
        _date.value = utc
    }

    fun setTime(pair: Pair<Int, Int>?) {
        _time.value = pair
    }

    fun setTaskReminder() {
        if (_date.value == null || _time.value == null) {
            _isDateTimeSelected.value = Event(false)
        } else {
            _isDateTimeSelected.value = Event(true)
            val dateTimeInMillis = dateTimeHelper.groupDateTime(
                _date.value!!,
                _time.value?.first!!,
                _time.value?.second!!,
            )
            setAlarm(dateTimeInMillis)
            updateTaskReminder(dateTimeInMillis)
            setFragmentResult(dateTimeInMillis)
        }
    }

    private fun setAlarm(dateTimeInMillis: Long) {
        _currentTask.value?.let {
            notificationHelper.cancel(it.pendingIntentRequestCode!!)
            reminderManager.setExactReminder(
                dateTimeInMillis,
                it
            )
        }
    }

    private fun updateTaskReminder(dateTimeInMillis: Long?) {
        _currentTask.value?.let { task ->
            tasksRepository.updateTaskReminder(task.id, dateTimeInMillis)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { },
                    { }
                )
        }
    }

    private fun setFragmentResult(dateTimeInMillis: Long) {
        _timeMillis.value = dateTimeInMillis
    }


}