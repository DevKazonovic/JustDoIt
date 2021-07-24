package com.devkazonovic.projects.mytasks.presentation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.holder.DataState
import com.devkazonovic.projects.mytasks.help.holder.Event
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_MAIN
import com.devkazonovic.projects.mytasks.help.util.handleResult
import com.devkazonovic.projects.mytasks.service.AlarmHelper
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import com.devkazonovic.projects.mytasks.service.TaskNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Scheduler
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val dateTimeHelper: DateTimeHelper,
    private val taskNotificationManager: TaskNotificationManager,
    private val reminderHelper: AlarmHelper,
    private val tasksRepository: ITasksRepository,
    @Named(SCHEDULER_MAIN) private val mainScheduler: Scheduler,
    @Named(SCHEDULER_IO) private val ioScheduler: Scheduler
) : ViewModel() {

    private val _navigateBack = MutableLiveData<Event<Boolean>>()
    val navigateBack: LiveData<Event<Boolean>> get() = _navigateBack

    private val _dataState = MutableLiveData<Event<DataState<Int>>>()
    private val _task = MutableLiveData<Task>()
    private val _category = MutableLiveData<Category>()
    private val _categories = MutableLiveData<List<Category>>()

    init {
        Timber.d("Init")
    }

    fun start(taskID: Long) {
        tasksRepository.getTask(taskID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(
                    result,
                    { task -> _task.postValue(task) },
                    { showSnackbarErrorMessage(R.string.unKnownError) }
                )
            }
    }

    fun deleteTask() {
        _task.value?.let { task ->
            tasksRepository.deleteTask(task)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { _navigateBack.value = Event(true) },
                    { showSnackbarErrorMessage(R.string.task_delete_fail) }
                )
        }
    }

    fun updateTask(title: String, detail: String, isCompleted: Boolean, reminderDate: Long?) {
        _task.value?.let { task ->
            val newTask = task.copy(
                title = title,
                detail = detail,
                isCompleted = isCompleted,
                reminderDate = reminderDate,
                listID = _category.value?.id!!,
            )
            newTask.reminderDate?.let { resetAlarm(newTask, it) }
            tasksRepository.updateTask(newTask)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { showSnackbarToastMessage(R.string.task_update_success) },
                    { showSnackbarErrorMessage(R.string.task_update_fail) }
                )
        }
    }

    private fun resetAlarm(task: Task, timeInMillis: Long) {
        task.pendingIntentRequestCode?.let {
            reminderHelper.setExactReminder(timeInMillis, task)
            updateNotification(task)
        }
    }

    private fun updateNotification(task: Task) {
        taskNotificationManager.update(
            task.pendingIntentRequestCode!!,
            task.title,
            task.detail,
            task.id
        )
    }

    fun removeReminder() {
        _task.value?.let { task ->
            task.pendingIntentRequestCode?.let {
                reminderHelper.cancelReminder(it)
                taskNotificationManager.cancel(it)
            }
            tasksRepository.updateTaskReminder(task.id, null)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { start(_task.value?.id!!) },
                    { }
                )
        }
    }

    fun cancelNotification(notificationID: Int) {
        taskNotificationManager.cancel(notificationID)
    }

    fun getCategory(listID: Long) {
        tasksRepository.getCategoryById(listID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(result,
                    { category -> _category.postValue(category) },
                    { showSnackbarErrorMessage(R.string.unKnownError) }
                )
            }
    }

    fun getCategories() {
        tasksRepository.getCategories()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(result,
                    { categories -> _categories.postValue(categories) },
                    { showSnackbarErrorMessage(R.string.unKnownError) }
                )
            }
    }

    fun updateCurrentTaskList(newListId: Long) {
        getCategory(newListId)
    }

    val task: LiveData<Task> get() = _task
    val category: LiveData<Category> get() = _category
    val lists: LiveData<List<Category>> get() = _categories
    val dataState: LiveData<Event<DataState<Int>>> get() = _dataState


    private fun showSnackbarErrorMessage(message: Int) {
        _dataState.value = Event(DataState.ErrorState(message))
    }

    private fun showSnackbarToastMessage(message: Int) {
        _dataState.value = Event(DataState.ErrorState(message))
    }
}