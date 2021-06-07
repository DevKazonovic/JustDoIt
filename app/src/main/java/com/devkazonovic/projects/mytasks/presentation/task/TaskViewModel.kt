package com.devkazonovic.projects.mytasks.presentation.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList
import com.devkazonovic.projects.mytasks.domain.repository.TasksRepository
import com.devkazonovic.projects.mytasks.domain.repository.mapToEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _task = MutableLiveData<Task>()
    private val _taskList = MutableLiveData<TaskList>()
    private val _lists = MutableLiveData<List<TaskList>>()

    val task: LiveData<Task> get() = _task
    val taskList: LiveData<TaskList> get() = _taskList
    val lists: LiveData<List<TaskList>> get() = _lists

    init {
        Timber.d("Init")
    }

    fun getTask(taskID: Long) {
        tasksRepository.getTask(taskID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { task ->
                    _task.postValue(task)
                },
                { error -> Timber.d(error) }
            )
    }

    fun deleteTask() {
        _task.value?.let { task ->
            tasksRepository.delete(task.mapToEntity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { Timber.d("Task Deleted") },
                    { error -> Timber.d(error) }
                )
        }
    }

    fun updateTask(newTask: Task) {
        _task.value?.let { task ->
            tasksRepository.update(
                task.copy(
                    title = newTask.title,
                    detail = newTask.detail,
                    isCompleted = newTask.isCompleted,
                    listID = _taskList.value?.id!!
                ).mapToEntity()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { Timber.d("Task Updated") },
                    { error -> Timber.d(error) }
                )

        }

    }

    fun getTaskList(listID: Long) {
        tasksRepository.getTasksList(listID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list -> _taskList.postValue(list) },
                { error -> Timber.d(error) }
            )
    }

    fun getLists() {
        tasksRepository.getAllLists()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lists -> _lists.postValue(lists) },
                { error -> Timber.d(error) }
            )
    }

    fun updateCurrentTaskList(newListId: Long) {
        getTaskList(newListId)
    }

}