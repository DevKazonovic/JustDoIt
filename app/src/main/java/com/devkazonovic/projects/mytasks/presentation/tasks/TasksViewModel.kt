package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.data.db.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.TasksRepository
import com.devkazonovic.projects.mytasks.domain.booleanToInt
import com.devkazonovic.projects.mytasks.domain.mapToEntity
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber

class TasksViewModel(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _tasksLists = MutableLiveData<List<TaskList>>()

    private val _currentTasksList = MutableLiveData<TaskList>()
    private val _tasks = MutableLiveData<List<Task>>()
    private val _completedTasks = MutableLiveData<List<Task>>()


    private val disposableCrudOperations = CompositeDisposable()
    private val disposableTasksObservables = CompositeDisposable()


    init {
        Timber.d("Init")
    }

    fun updateTasks() {
        getTasks()
        getCompletedTasks()
    }


    fun markTaskAsCompleted(taskID: Long, isCompleted: Boolean) {
        tasksRepository.taskComplete(taskID, booleanToInt(isCompleted))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { },
                { error -> Timber.d("$error") }
            )

    }

    fun getTasksLists() {
        tasksRepository.getAllTasksLists()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lists -> _tasksLists.postValue(lists) },
                { error -> Timber.d(error) }
            )
    }

    fun getTasksList(listID: Long) {
        tasksRepository.getTasksList(listID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list -> _currentTasksList.value = list },
                { error -> Timber.d(error) }
            )
    }

    fun changeCurrentTasksList(listID: Long) {
        getTasksList(listID)
        disposableTasksObservables.clear()
    }

    fun updateCurrentTasksListName(newName: String) {
        tasksRepository.update(
            TaskList(
                id = _currentTasksList.value?.id!!,
                name = newName
            ).mapToEntity()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Task Updated") },
                { error -> Timber.d(error) }
            ).addTo(disposableCrudOperations)
    }

    fun createNewTasksList(name: String) {
        tasksRepository.insert(TaskListEntity(name = name))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("NewList Created") },
                { error -> Timber.d(error) }
            ).addTo(disposableCrudOperations)
    }


    fun saveTask(title: String, detail: String) {
        _currentTasksList.value?.id?.let {
            tasksRepository.insert(

                Task(
                    title = title,
                    detail = detail,
                    listID = it
                ).mapToEntity()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { Timber.d("Task Saved") },
                    { error -> Timber.d(error) }
                ).addTo(disposableCrudOperations)
        }
    }

    fun getTasks() {
        _currentTasksList.value?.id?.let { id ->
            Timber.d("--id[${id}]")
            tasksRepository.getUnCompletedTasks(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        _tasks.postValue(list)
                    },
                    { error -> Timber.d("$error") }
                ).addTo(disposableTasksObservables)
        }
    }

    fun getCompletedTasks() {
        _currentTasksList.value?.id?.let { id ->
            tasksRepository.getCompletedTasks(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list ->
                        _completedTasks.postValue(list)
                    },
                    { error -> Timber.d("$error") }
                ).addTo(disposableTasksObservables)
        }
    }


    val tasks: LiveData<List<Task>> get() = _tasks
    val completedTasks: LiveData<List<Task>> get() = _completedTasks
    val currentTaskList: LiveData<TaskList> get() = _currentTasksList
    val tasksLists: LiveData<List<TaskList>> get() = _tasksLists


    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }
}