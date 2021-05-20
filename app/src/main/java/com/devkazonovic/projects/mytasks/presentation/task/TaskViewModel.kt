package com.devkazonovic.projects.mytasks.presentation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.domain.TasksRepository
import com.devkazonovic.projects.mytasks.domain.mapToEntity
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class TaskViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    private val _taskID = MutableLiveData<Long>()

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> get() = _task

    private val _taskList = MutableLiveData<TaskList>()
    val taskList: LiveData<TaskList> get() = _taskList

    fun getTask(taskID: Long) {
        tasksRepository.getTask(taskID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { task ->
                    _task.postValue(task)
                    _taskID.postValue(task.id)
                },
                { error -> Timber.d(error) }
            )
    }

    fun getTasksList(listID: Long) {
        tasksRepository.getTasksList(listID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list -> _taskList.value = list },
                { error -> Timber.d(error) }
            )
    }

    fun updateTask(newTask: Task) {
        _task.value?.let { task ->
            tasksRepository.update(Task(
                id = task.id,
                title = newTask.title,
                detail = newTask.detail,
                listID = _taskList.value?.id!!,
                date = task.date,
                completedAt = task.date,
                isCompleted = task.isCompleted
            ).mapToEntity())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Task Updated") },
                { error -> Timber.d(error) }
            )

        }

    }

}