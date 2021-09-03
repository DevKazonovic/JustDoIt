package com.devkazonovic.projects.justdoit.presentation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.data.local.preference.ISettingSharedPreference
import com.devkazonovic.projects.justdoit.data.repository.ITasksRepository
import com.devkazonovic.projects.justdoit.domain.RxScheduler
import com.devkazonovic.projects.justdoit.domain.holder.DataState
import com.devkazonovic.projects.justdoit.domain.holder.Event
import com.devkazonovic.projects.justdoit.domain.model.Category
import com.devkazonovic.projects.justdoit.domain.model.Repeat
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.domain.model.TimeFormat
import com.devkazonovic.projects.justdoit.help.util.handleResult
import com.devkazonovic.projects.justdoit.service.DateTimeHelper
import com.devkazonovic.projects.justdoit.service.TaskAlarmManager
import com.devkazonovic.projects.justdoit.service.TaskNotificationManager
import com.devkazonovic.projects.justdoit.service.TaskRepeatAlarmManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val tasksRepository: ITasksRepository,
    private val taskAlarmManager: TaskAlarmManager,
    private val taskRepeatAlarmManager: TaskRepeatAlarmManager,
    private val taskNotificationManager: TaskNotificationManager,
    private val dateTimeHelper: DateTimeHelper,
    private val settingSharedPreference: ISettingSharedPreference,
    rxScheduler: RxScheduler,
) : ViewModel() {

    /**RxJava Tools*/
    private val mainScheduler: Scheduler = rxScheduler.mainScheduler()
    private val ioScheduler: Scheduler = rxScheduler.ioScheduler()
    private val disposableGeneral = CompositeDisposable()

    /**LiveData*/
    private val _currentTask = MutableLiveData<Task>()
    private val _currentCategory = MutableLiveData<Category>()
    private val _categories = MutableLiveData<List<Category>>()
    private val _date = MutableLiveData<Long?>()
    private val _time = MutableLiveData<Pair<Int, Int>?>()
    private val _repeat = MutableLiveData<Repeat?>()
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

    private val _navigateBack = MutableLiveData<Event<Boolean>>()
    private val _dataState = MutableLiveData<Event<DataState<Int>>>()

    init {
        Timber.d("Init")
    }

    override fun onCleared() {
        super.onCleared()
        disposableGeneral.clear()
    }

    fun start(taskID: Long) {
        tasksRepository.getTask(taskID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(
                    result,
                    { task -> initLiveData(task) },
                    { showSnackBarErrorMessage(R.string.unKnownError) }
                )
            }
            .addTo(disposableGeneral)
    }

    fun getTimeFormat(): TimeFormat =
        settingSharedPreference.getTimeFormat()


    private fun initLiveData(task: Task) {
        _currentTask.postValue(task)
        updateTaskCategory(task.categoryId)
        initTaskDueDateLiveData(task)
    }

    fun deleteTask() {
        _currentTask.value?.let { task ->
            tasksRepository.deleteTask(task)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { _navigateBack.value = Event(true) },
                    { showSnackBarErrorMessage(R.string.task_delete_fail) }
                ).addTo(disposableGeneral)

        }
    }

    fun updateTask(title: String, detail: String, isCompleted: Boolean) {
        _currentTask.value?.let { task ->
            val dueDateInMillis = calcDueDate()
            val newTask = task.copy(
                title = title,
                detail = detail,
                isCompleted = isCompleted,
                dueDate = dueDateInMillis,
                isAllDay = isAllDay(),
                categoryId = _currentCategory.value?.id!!,
                repeatType = _repeat.value?.type,
                repeatValue = _repeat.value?.number,
                nextDueDate = calcNextDueDate(_repeat.value, dueDateInMillis)
            )
            dueDateInMillis?.let { setAlarm(it) }
            updateNotification(newTask)
            tasksRepository.updateTask(newTask)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    {},
                    { showSnackBarErrorMessage(R.string.task_update_fail) }
                ).addTo(disposableGeneral)
        }
    }

    fun updateTaskCategory(listID: Long) {
        tasksRepository.getCategoryById(listID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(result,
                    { category -> _currentCategory.postValue(category) },
                    { showSnackBarErrorMessage(R.string.unKnownError) }
                )
            }
            .addTo(disposableGeneral)
    }

    fun getCategories() {
        tasksRepository.getCategories()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(result,
                    { categories -> _categories.postValue(categories) },
                    { showSnackBarErrorMessage(R.string.unKnownError) }
                )
            }
            .addTo(disposableGeneral)
    }

    fun setDate(utc: Long?) {
        _date.value = utc
    }

    fun setTime(pair: Pair<Int, Int>?) {
        _time.value = pair
    }

    fun setRepeat(repeat: Repeat?) {
        _repeat.value = repeat
    }

    fun cancelNotification(notificationID: Int) {
        taskNotificationManager.cancelDueDateNotification(notificationID)
    }

    private fun initTaskDueDateLiveData(task: Task) {
        if (task.dueDate == null) {
            _date.value = null
        } else {
            _date.value = dateTimeHelper.getDateInMillis(task.dueDate)
            if (task.isAllDay) {
                _time.value = null
            } else {
                _time.value = dateTimeHelper.getTimeInHourMinute(task.dueDate)
            }
            if (task.repeatType != null) {
                _repeat.value = Repeat(task.repeatType, task.repeatValue)
            } else {
                _repeat.value = null
            }
        }
    }

    private fun calcDueDate(): Long? {
        return if (_date.value != null) {
            if (_time.value == null) {
                dateTimeHelper.groupDateTime(_date.value!!, 8, 0)
            } else {
                dateTimeHelper.groupDateTime(_date.value!!,
                    _time.value?.first!!,
                    _time.value?.second!!)
            }
        } else {
            null
        }
    }

    private fun calcNextDueDate(repeat: Repeat?, dueDateInMillis: Long?): Long? {
        return if (dueDateInMillis != null && repeat != null && repeat.type != null && repeat.number != null) {
            taskRepeatAlarmManager.addRepeatValue(
                repeat.type,
                repeat.number,
                dateTimeHelper.fromLongToLocalDate(dueDateInMillis),
                dateTimeHelper.getTimeInHourMinute(dueDateInMillis)
            )
        } else null
    }

    private fun isAllDay(): Boolean {
        return _time.value == null || _date.value == null
    }

    private fun setAlarm(dateTimeInMillis: Long) {
        _currentTask.value?.let { task ->
            val notificationId = task.alarmId
            val dueDate = task.dueDate
            if (notificationId != null && dueDate != null) {
                taskNotificationManager.cancelDueDateNotification(notificationId)
                taskAlarmManager.setDueDateAlarm(dateTimeInMillis, task)
            }
        }
    }

    private fun updateNotification(task: Task) {
        task.dueDate?.let { dueDateInMillis ->
            taskNotificationManager.updateDueDateNotification(
                task.alarmId!!,
                task.id,
                task.title,
                task.detail,
                dateTimeHelper.showTime(dateTimeHelper.getTimeInHourMinute(dueDateInMillis))
            )
        }
    }

    private fun showSnackBarErrorMessage(message: Int) {
        _dataState.value = Event(DataState.ErrorState(message))
    }


    val currentTask: LiveData<Task> get() = _currentTask
    val currentTaskCategory: LiveData<Category> get() = _currentCategory
    val categories: LiveData<List<Category>> get() = _categories
    val date: LiveData<Long?> get() = _date
    val time: LiveData<Pair<Int, Int>?> get() = _time
    val repeat: LiveData<Repeat?> get() = _repeat

    val dataState: LiveData<Event<DataState<Int>>> get() = _dataState
    val navigateBack: LiveData<Event<Boolean>> get() = _navigateBack
}