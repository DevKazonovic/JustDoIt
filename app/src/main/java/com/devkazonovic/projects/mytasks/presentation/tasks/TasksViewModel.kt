package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.data.local.preference.IMainSharedPreference
import com.devkazonovic.projects.mytasks.data.repository.ITasksRepository
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.holder.Event
import com.devkazonovic.projects.mytasks.help.holder.InputError
import com.devkazonovic.projects.mytasks.help.holder.Result
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_MAIN
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.threeten.bp.OffsetDateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: ITasksRepository,
    private val sharedPreferences: IMainSharedPreference,
    @Named(SCHEDULER_MAIN) private val mainScheduler: Scheduler,
    @Named(SCHEDULER_IO) private val ioScheduler: Scheduler
) : ViewModel() {


    private val _isDownloading = MutableLiveData(false)
    private val _snackBarEvent = MutableLiveData<Event<Int>>()
    private val _userInputErrorEvent = MutableLiveData<Event<InputError>>()
    private val _mainViewErrorEvent = MutableLiveData<Event<Int>>()
    private val _snackBarErrorEvent = MutableLiveData<Event<Int>>()


    private val _categories = MutableLiveData<List<Category>>()
    private val _currentCategory = MutableLiveData<Category>()
    private val _unCompletedTasks = MutableLiveData<List<Task>>()
    private val _completedTasks = MutableLiveData<List<Task>>()

    private val disposableCrudOperations = CompositeDisposable()
    private val disposableTasksObservables = CompositeDisposable()

    init {
        Timber.d("Init")
        _isDownloading.value = true
        updateCurrentCategory()
    }

    fun observeTasks() {
        getUnCompletedTasks()
        getCompletedTasks()
        _isDownloading.value = false
    }

    private fun getUnCompletedTasks() {
        _currentCategory.value?.id?.let { id ->
            tasksRepository.getUnCompletedTasks(id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe { result ->
                    handleResult(result,
                        { _unCompletedTasks.postValue(it) },
                        {
                            setMainViewMassage(R.string.unKnownError)
                        }
                    )
                }.addTo(disposableTasksObservables)
        }
    }

    private fun getCompletedTasks() {
        _currentCategory.value?.id?.let { id ->
            tasksRepository.getCompletedTasks(id)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe { result ->
                    handleResult(result,
                        { _completedTasks.postValue(it) },
                        { setMainViewMassage(it) }
                    )
                }.addTo(disposableTasksObservables)
        }
    }

    fun deleteAllCompletedTasks() {
        tasksRepository.clearCompletedTasks()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { setSnackBarMassage(R.string.completed_tasks_delete_success) },
                { setSnackBarErrorMassage(R.string.completed_tasks_delete_fail) }
            )

    }

    fun saveTask(title: String, detail: String) {
        if (title.isEmpty())
            setUserInputErrorMassage(InputError.TaskTitleInput(R.string.task_save_title_empty))
        else {
            _currentCategory.value?.id?.let { currentTasksListId ->
                tasksRepository.addNewTask(
                    Task(
                        title = title,
                        detail = detail,
                        listID = currentTasksListId,
                        date = OffsetDateTime.now()
                    )
                )
                    .subscribeOn(ioScheduler)
                    .observeOn(mainScheduler)
                    .subscribe(
                        { setSnackBarMassage(R.string.task_save_success) },
                        { setSnackBarErrorMassage(R.string.task_save_fail) }
                    ).addTo(disposableCrudOperations)
            }
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
                { setSnackBarMassage(R.string.task_completed) },
                { setSnackBarErrorMassage(R.string.unKnownError) }
            )
    }

    private fun unCompleteTask(taskID: Long) {
        tasksRepository.markTaskAsUnCompleted(taskID)
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { setSnackBarMassage(R.string.task_active) },
                { setSnackBarErrorMassage(R.string.unKnownError) }
            )
    }

    fun getCategories() {
        tasksRepository.getCategories()
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe { result ->
                handleResult(result,
                    { _categories.postValue(it) },
                    { }
                )
            }
    }

    fun createNewCategory(name: String) {
        tasksRepository.addNewCategory(Category(id = -1, name))
            .subscribeOn(ioScheduler)
            .observeOn(mainScheduler)
            .subscribe(
                { setSnackBarMassage(R.string.category_save_success) },
                { setSnackBarErrorMassage(R.string.category_save_fail) }
            ).addTo(disposableCrudOperations)
    }

    fun updateCurrentCategory(newListID: Long = 0) {
        if (sharedPreferences.saveCurrentTasksList(newListID)) {
            tasksRepository.getCategoryById(newListID)
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe { result ->
                    handleResult(result,
                        {
                            _currentCategory.value = it
                            setSnackBarMassage(R.string.category_update_success)
                        },
                        { setSnackBarMassage(R.string.category_update_fail) }
                    )
                }
            disposableTasksObservables.clear()
        }
    }

    fun updateCurrentCategoryName(newName: String) {
        _currentCategory.value?.let { list ->
            tasksRepository.updateCategory(
                Category(
                    id = list.id,
                    name = newName,
                    isDefault = list.isDefault
                )
            )
                .subscribeOn(ioScheduler)
                .observeOn(mainScheduler)
                .subscribe(
                    { updateCurrentCategory(list.id) },
                    { Timber.d(it) }
                ).addTo(disposableCrudOperations)
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
                    ).addTo(disposableCrudOperations)
            } else {
                setSnackBarMassage(R.string.warning_delete_default_list)
            }
        }
    }

    private fun <T> handleResult(
        result: Result<T>,
        onSuccess: (data: T) -> Unit,
        onError: (message: Int) -> Unit
    ) {
        when (result) {
            is Result.Success -> onSuccess(result.value)
            is Result.Failure -> {
                Timber.d(result.throwable)
                onError(R.string.unKnownError)
            }
        }
    }

    private fun setUserInputErrorMassage(value: InputError) {
        _userInputErrorEvent.value = Event(value)
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

    override fun onCleared() {
        super.onCleared()
        Timber.d("onCleared")
    }

    val unCompletedTasks: LiveData<List<Task>> get() = _unCompletedTasks
    val completedTasks: LiveData<List<Task>> get() = _completedTasks
    val currentCategory: LiveData<Category> get() = _currentCategory
    val tasksLists: LiveData<List<Category>> get() = _categories
    val isDownloading: LiveData<Boolean> get() = _isDownloading
    val userInputErrorEvent: LiveData<Event<InputError>> get() = _userInputErrorEvent
    val mainViewErrorEvent: LiveData<Event<Int>> get() = _mainViewErrorEvent
    val snackBarErrorEvent: LiveData<Event<Int>> get() = _snackBarErrorEvent
    val snackBarEvent: LiveData<Event<Int>> get() = _snackBarEvent
}