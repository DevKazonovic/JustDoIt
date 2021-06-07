package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.data.local.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList
import com.devkazonovic.projects.mytasks.domain.repository.TasksRepository
import com.devkazonovic.projects.mytasks.domain.repository.mapToEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val sharedPreferences: MySharedPreferences
) : ViewModel() {

    private val _tasksLists = MutableLiveData<List<TaskList>>()
    private val _currentTasksList = MutableLiveData<TaskList>()

    private val _tasks = MutableLiveData<List<Task>>()
    private val _completedTasks = MutableLiveData<List<Task>>()

    private val disposableCrudOperations = CompositeDisposable()
    private val disposableTasksObservables = CompositeDisposable()

    init {
        Timber.d("Init")
        updateCurrentList(sharedPreferences.getCurrentTasksList())
    }

    fun updateTasks() {
        getUnCompletedTasks()
        getCompletedTasks()
    }

    private fun getUnCompletedTasks() {
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

    private fun getCompletedTasks() {
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

    fun deleteAllCompletedTasks() {
        tasksRepository.clearCompletedTasks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("Success") },
                { error -> Timber.d("$error") }
            )

    }

    fun saveTask(title: String, detail: String) {
        _currentTasksList.value?.id?.let { currentTasksListId ->
            tasksRepository.insert(
                Task(
                    title = title,
                    detail = detail,
                    listID = currentTasksListId,
                    date = OffsetDateTime.now()
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

    fun markTaskAsCompleted(taskID: Long, isCompleted: Boolean) {
        tasksRepository.getTask(taskID)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { task ->
                    tasksRepository.markTaskAsCompleted(
                        task.copy(completedAt = OffsetDateTime.now(), isCompleted = isCompleted)
                            .mapToEntity()

                    ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                            { Timber.d("Task Updated") },
                            { error -> Timber.d("$error") }
                        )
                },
                { error -> Timber.d("$error") }
            )


    }

    fun getLists() {
        tasksRepository.getAllLists()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { lists -> _tasksLists.postValue(lists) },
                { error -> Timber.d(error) }
            )
    }

    fun createNewList(name: String) {
        tasksRepository.insert(TaskListEntity(name = name))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Timber.d("NewList Created") },
                { error -> Timber.d(error) }
            ).addTo(disposableCrudOperations)
    }

    fun updateCurrentList(newListID: Long) {
        if (sharedPreferences.saveCurrentTasksList(newListID)) {
            tasksRepository.getTasksList(newListID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { list -> _currentTasksList.value = list },
                    { error -> Timber.d(error) }
                )
            disposableTasksObservables.clear()
        }
    }

    fun updateCurrentListName(newName: String) {
        _currentTasksList.value?.let { list ->
            tasksRepository.update(
                TaskList(
                    id = list.id,
                    name = newName,
                    isDefault = list.isDefault
                ).mapToEntity()
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { updateCurrentList(list.id) },
                    { error -> Timber.d(error) }
                ).addTo(disposableCrudOperations)
        }
    }

    fun deleteCurrentList() {
        _currentTasksList.value?.let {
            if (!it.isDefault) {
                tasksRepository.delete(it.mapToEntity())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { updateCurrentList(0) },
                        { error -> Timber.d("$error") }
                    ).addTo(disposableCrudOperations)
            } else {
                Timber.d("You can't delete default list")
            }
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