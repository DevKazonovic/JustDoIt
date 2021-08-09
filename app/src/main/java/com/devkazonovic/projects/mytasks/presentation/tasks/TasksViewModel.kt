package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.data.local.preference.IMainSharedPreference
import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.IRxScheduler
import com.devkazonovic.projects.mytasks.domain.holder.Event
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.util.handleResult
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.ActiveTask
import com.devkazonovic.projects.mytasks.service.DateTimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.threeten.bp.Instant
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dateTimeHelper: DateTimeHelper,
    private val tasksRepository: ITasksRepository,
    private val sharedPreferences: IMainSharedPreference,
    rxScheduler: IRxScheduler,
) : ViewModel() {

    /**RxJava Tools*/
    private val mainScheduler = rxScheduler.mainScheduler()
    private val ioScheduler = rxScheduler.ioScheduler()
    private val disposableGeneral = CompositeDisposable()
    private val disposableTasksObservables = CompositeDisposable()

    /**Events*/
    private val _snackBarEvent = MutableLiveData<Event<Int>>()
    private val _mainViewErrorEvent = MutableLiveData<Event<Int>>()
    private val _snackBarErrorEvent = MutableLiveData<Event<Int>>()

    /**Categories*/
    private val _categories = MutableLiveData<List<Category>>()
    private val _currentCategory = MutableLiveData<Category>()

    /**Tasks*/
    private val _activeTasks = MutableLiveData<List<ActiveTask>>()
    private val _completedTasks = MutableLiveData<List<Task>>()

    init {
        updateCurrentCategory()
    }

    override fun onCleared() {
        super.onCleared()
        disposableTasksObservables.clear()
        disposableGeneral.clear()
    }

    fun observeTasks() {
        getUnCompletedTasks()
        getCompletedTasks()
    }

    private fun getUnCompletedTasks() {
        _currentCategory.value?.id?.let { id ->
            tasksRepository.getUnCompletedTasks(id)
                .flatMap { input ->
                    when (input) {
                        is Result.Success -> {
                            val result = mapToActiveTasks(input.value)
                            Flowable.just(Result.Success(result))
                        }

                        is Result.Failure -> {
                            Flowable.just(Result.Failure(input.throwable))
                        }
                    }
                }
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe { result ->
                    handleResult(result,
                        { _activeTasks.postValue(it) },
                        { setMainViewMassage(R.string.unKnownError) }
                    )
                }
                .addTo(disposableTasksObservables)
        }
    }

    private fun mapToActiveTasks(list: List<Task>): List<ActiveTask> {
        val result = mutableListOf<ActiveTask>()
        val overDues = mutableListOf<Task>()
        val actives = mutableListOf<Task>()
        val noDueDate = mutableListOf<Task>()

        list.forEach { task ->
            if (task.dueDate == null) {
                noDueDate.add(task)
            } else {
                if (task.isAllDay) {
                    if (dateTimeHelper.isDateBeforeNow(task.dueDate)) {
                        overDues.add(task)
                    } else {
                        actives.add(task)
                    }
                } else {
                    if (dateTimeHelper.isBeforeNow(task.dueDate)) {
                        overDues.add(task)
                    } else {
                        actives.add(task)
                    }
                }
            }
        }

        result += if (overDues.isNotEmpty())
            listOf(ActiveTask.ItemHeader(type = ActiveTask.HeaderType.OVERDUE)) +
                    overDues.map { ActiveTask.ItemTask(it) }
                        .sortedBy { itemTask -> Instant.ofEpochMilli(itemTask.task.dueDate!!) }
        else emptyList()


        result += if (actives.isNotEmpty())
            listOf(ActiveTask.ItemHeader(type = ActiveTask.HeaderType.ACTIVE)) +
                    actives.map { ActiveTask.ItemTask(it) }
                        .sortedBy { itemTask -> Instant.ofEpochMilli(itemTask.task.dueDate!!) }
        else emptyList()


        result += if (noDueDate.isNotEmpty())
            listOf(ActiveTask.ItemHeader(type = ActiveTask.HeaderType.NO_DATE)) + noDueDate.map {
                ActiveTask.ItemTask(it)
            }
        else emptyList()

        return result
    }

    private fun getCompletedTasks() {
        _currentCategory.value?.id?.let { id ->
            tasksRepository.getCompletedTasks(id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe { result ->
                    handleResult(result,
                        { _completedTasks.postValue(it) },
                        { setMainViewMassage(R.string.unKnownError) }
                    )
                }
                .addTo(disposableTasksObservables)
        }
    }

    fun deleteAllCompletedTasks() {
        tasksRepository.clearCompletedTasks()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { setSnackBarMassage(R.string.completed_tasks_delete_success) },
                { setSnackBarErrorMassage(R.string.completed_tasks_delete_fail) }
            ).addTo(disposableGeneral)
    }

    fun saveTask(title: String, detail: String) {
        _currentCategory.value?.id?.let { currentTasksListId ->
            tasksRepository.addNewTask(
                Task(
                    title = title,
                    detail = detail,
                    categoryId = currentTasksListId,
                    date = OffsetDateTime.now()
                )
            )
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { },
                    { setSnackBarErrorMassage(R.string.task_save_fail) }
                ).addTo(disposableGeneral)
        }
    }

    fun markTaskAsCompleted(taskID: Long, isCompleted: Boolean) {
        if (isCompleted) completeTask(taskID)
        else unCompleteTask(taskID)
    }

    private fun completeTask(taskID: Long) {
        tasksRepository.markTaskAsCompleted(taskID, OffsetDateTime.now())
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { },
                { setSnackBarErrorMassage(R.string.unKnownError) }
            ).addTo(disposableGeneral)
    }

    private fun unCompleteTask(taskID: Long) {
        tasksRepository.markTaskAsUnCompleted(taskID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { },
                { setSnackBarErrorMassage(R.string.unKnownError) }
            ).addTo(disposableGeneral)
    }

    fun getCategories() {
        tasksRepository.getCategories()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(result,
                    { _categories.postValue(it) },
                    {
                        _categories.postValue(listOf(Category.DEFAULT_LIST))
                        setSnackBarErrorMassage(R.string.categories_fetch_error)
                    }
                )
            }
            .addTo(disposableGeneral)
    }

    fun createNewCategory(name: String) {
        tasksRepository.addNewCategory(Category(name = name))
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { setSnackBarMassage(R.string.category_save_success) },
                { setSnackBarErrorMassage(R.string.category_save_fail) }
            ).addTo(disposableGeneral)
    }

    fun updateCurrentCategory(newListID: Long = 0) {
        if (sharedPreferences.saveCurrentTasksList(newListID)) {
            tasksRepository.getCategoryById(newListID)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe { result ->
                    handleResult(result,
                        { _currentCategory.value = it },
                        { setSnackBarMassage(R.string.category_update_fail) }
                    )
                }
            disposableTasksObservables.clear()
        }
    }

    fun updateCurrentCategoryName(newName: String) {
        _currentCategory.value?.let { category ->
            tasksRepository.updateCategory(category.copy(name = newName))
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { updateCurrentCategory(category.id) },
                    { setSnackBarMassage(R.string.category_update_name_fail) }
                ).addTo(disposableGeneral)
        }
    }

    fun deleteCurrentCategory() {
        _currentCategory.value?.let { category ->
            if (!category.isDefault) {
                tasksRepository.deleteCategory(category)
                    .subscribeOn(ioScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(
                        {
                            updateCurrentCategory(0)
                            setSnackBarMassage(R.string.category_delete_success)
                        },
                        { setSnackBarMassage(R.string.category_delete_fail) }
                    ).addTo(disposableGeneral)
            }
        }
    }

    private fun setMainViewMassage(value: Int) {
        _mainViewErrorEvent.value = Event(value)
    }

    private fun setSnackBarErrorMassage(value: Int) {
        _snackBarErrorEvent.value = Event(value)
    }

    private fun setSnackBarMassage(value: Int) {
        _snackBarEvent.value = Event(value)
    }


    val currentCategory: LiveData<Category> get() = _currentCategory
    val categories: LiveData<List<Category>> get() = _categories
    val activeTasks: LiveData<List<ActiveTask>> get() = _activeTasks
    val completedTasks: LiveData<List<Task>> get() = _completedTasks
    val mainViewErrorEvent: LiveData<Event<Int>> get() = _mainViewErrorEvent
    val snackBarErrorEvent: LiveData<Event<Int>> get() = _snackBarErrorEvent
    val snackBarEvent: LiveData<Event<Int>> get() = _snackBarEvent
}