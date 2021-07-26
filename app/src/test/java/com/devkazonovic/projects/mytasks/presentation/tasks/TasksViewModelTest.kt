package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.data.local.preference.MySharedPreferences
import com.devkazonovic.projects.mytasks.data.repository.TasksRepository
import com.devkazonovic.projects.mytasks.domain.holder.Event
import com.devkazonovic.projects.mytasks.domain.holder.InputError
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.getOrAwaitValue
import com.google.common.truth.Truth
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.internal.schedulers.TrampolineScheduler
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*


private val task_active = Task(0, "Task", listID = 0)
private val task_completed = Task(0, "Task", listID = 0, isCompleted = true)
private val DEFAULT_TASK_LIST = Category(0, "MyList", true)
private val NOT_DEFAULT_LIST = Category(1, "New List", false)

@RunWith(MockitoJUnitRunner::class)
class TasksViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TasksViewModel

    @Mock
    private lateinit var tasksRepository: TasksRepository

    @Mock
    private lateinit var sharedPreference: MySharedPreferences

    @Before
    fun setUp() {
        setUpSharedPreferenceWithDefaultTaskList()
        viewModel = TasksViewModel(
            tasksRepository,
            sharedPreference,
            TrampolineScheduler.instance(),
            TrampolineScheduler.instance()
        )

    }

    private fun setUpSharedPreferenceWithDefaultTaskList() {
        whenever(sharedPreference.getCurrentTasksList()).thenReturn(0)
        whenever(sharedPreference.saveCurrentTasksList(any())).thenReturn(true)
        whenever(tasksRepository.getCategoryById(any())).thenReturn(
            Single.just(Result.Success(DEFAULT_TASK_LIST))
        )
    }

    @Test
    fun `when Adding NewTask withEmptyTitle, Don't Save Task & Emit InputErrorEvent`() {
        viewModel.saveTask("", "detail")

        verify(tasksRepository, never()).addNewTask(any())
        val errorEvent = viewModel.userInputErrorEvent.getOrAwaitValue()
        val expectedErrorEvent = Event(
            InputError.TaskTitleInput(R.string.task_save_title_empty)
        )

        MatcherAssert.assertThat(errorEvent, Matchers.instanceOf(expectedErrorEvent.javaClass))
        MatcherAssert.assertThat(errorEvent, Matchers.samePropertyValuesAs(expectedErrorEvent))
    }

    @Test
    fun `when Adding NewTask with Valid Title, Save Task & Emit SnackBarEvent`() {
        whenever(tasksRepository.addNewTask(any())).thenReturn(
            Completable.complete()
        )

        val task = Task(title = "Title", detail = "detail")
        viewModel.saveTask("Title", "detail")

        verify(tasksRepository).addNewTask(argWhere {
            it.title == task.title && it.detail == task.detail
        })

        val snackBarEvent = viewModel.snackBarEvent.getOrAwaitValue()
        val expectedSnackBar = Event(R.string.task_save_success)

        MatcherAssert.assertThat(snackBarEvent, Matchers.instanceOf(expectedSnackBar.javaClass))
        MatcherAssert.assertThat(snackBarEvent, Matchers.samePropertyValuesAs(expectedSnackBar))
    }

    @Test
    fun `when Loading Tasks, Show Loading Bar`() {
        val currentCategory = sharedPreference.getCurrentTasksList()
        val currentActiveTasks = listOf(task_active)
        val currentCompletedTasks = listOf(task_completed)

        whenever(tasksRepository.getCompletedTasks(currentCategory)).thenReturn(
            Flowable.just(Result.Success(currentCompletedTasks))
        )

        whenever(tasksRepository.getUnCompletedTasks(currentCategory)).thenReturn(
            Flowable.just(Result.Success(currentActiveTasks))
        )

        Truth.assertThat(viewModel.isDownloading.getOrAwaitValue()).isTrue()
        viewModel.observeTasks()
        Truth.assertThat(viewModel.isDownloading.getOrAwaitValue()).isFalse()

    }

    @Test
    fun `when Loading Tasks Return Success, Emit Date & Hide Progress bar`() {
        val currentCategory = sharedPreference.getCurrentTasksList()
        val currentActiveTasks = listOf(task_active)
        val currentCompletedTasks = listOf(task_completed)

        whenever(tasksRepository.getCompletedTasks(currentCategory)).thenReturn(
            Flowable.just(Result.Success(currentCompletedTasks))
        )

        whenever(tasksRepository.getUnCompletedTasks(currentCategory)).thenReturn(
            Flowable.just(Result.Success(currentActiveTasks))
        )

        viewModel.observeTasks()

        verify(tasksRepository).getCompletedTasks(eq(currentCategory))
        verify(tasksRepository).getUnCompletedTasks(eq(currentCategory))


        val completedTasks = viewModel.completedTasks.getOrAwaitValue()
        val activeTasks = viewModel.unCompletedTasks.getOrAwaitValue()

        MatcherAssert.assertThat(completedTasks, Matchers.hasItems(task_completed))
        MatcherAssert.assertThat(activeTasks, Matchers.hasItems(task_active))

        Truth.assertThat(viewModel.isDownloading.getOrAwaitValue()).isFalse()


    }

    @Test
    fun `when Loading Tasks Return Error, Emit MainErrorEvent & Hide Progress bar`() {
        val currentCategory = sharedPreference.getCurrentTasksList()

        whenever(tasksRepository.getCompletedTasks(currentCategory)).thenReturn(
            Flowable.just(Result.Failure(Exception("Error!")))
        )

        whenever(tasksRepository.getUnCompletedTasks(currentCategory)).thenReturn(
            Flowable.just(Result.Failure(Exception("Error!")))
        )

        viewModel.observeTasks()

        verify(tasksRepository).getCompletedTasks(eq(currentCategory))
        verify(tasksRepository).getUnCompletedTasks(eq(currentCategory))

        val expectedMainViewErrorEvent = Event(R.string.unKnownError)
        MatcherAssert.assertThat(
            viewModel.mainViewErrorEvent.getOrAwaitValue().getContentIfNotHandled(),
            Matchers.equalTo(expectedMainViewErrorEvent.getContentIfNotHandled())
        )
        Truth.assertThat(viewModel.isDownloading.getOrAwaitValue()).isFalse()

    }

    @Test
    fun `when Updating Current Category, Update SharedPreferences & Emit The New Category`() {
        whenever(tasksRepository.getCategoryById(NOT_DEFAULT_LIST.id)).thenReturn(
            Single.just(Result.Success(NOT_DEFAULT_LIST))
        )
        viewModel.updateCurrentCategory(NOT_DEFAULT_LIST.id)
        verify(sharedPreference).saveCurrentTasksList(NOT_DEFAULT_LIST.id)
        Truth.assertThat(viewModel.currentCategory.getOrAwaitValue().id)
            .isEqualTo(NOT_DEFAULT_LIST.id)
        Truth.assertThat(viewModel.snackBarEvent.getOrAwaitValue().getContentIfNotHandled())
            .isEqualTo(R.string.category_update_success)
    }

    @Test
    fun `when Updating Current Category Name, Emit The New Category & Emit SnackBarEvent`() {
        whenever(tasksRepository.getCategoryById(DEFAULT_TASK_LIST.id)).thenReturn(
            Single.just(Result.Success(DEFAULT_TASK_LIST.copy(name = "New Name")))
        )

        whenever(tasksRepository.updateCategory(DEFAULT_TASK_LIST.copy(name = "New Name"))).thenReturn(
            Completable.complete()
        )

        viewModel.updateCurrentCategoryName("New Name")

        verify(tasksRepository).updateCategory(
            DEFAULT_TASK_LIST.copy(name = "New Name")
        )

        Truth.assertThat(viewModel.currentCategory.getOrAwaitValue()).isEqualTo(
            DEFAULT_TASK_LIST.copy(name = "New Name")
        )
    }

    @Test
    fun `when Deleting Default List, Don't Delete it & Emit SnackBarEvent`() {
        viewModel.deleteCurrentCategory()
        verify(tasksRepository, never()).deleteCategory(any())

        Truth.assertThat(viewModel.snackBarEvent.getOrAwaitValue().getContentIfNotHandled())
            .isEqualTo(
                R.string.warning_delete_default_list
            )
    }

    @Test
    fun `when Deleting NOT Default List, Delete it & Emit SnackBarEvent & Update CurrentCategory To Default Category`() {

        whenever(sharedPreference.saveCurrentTasksList(NOT_DEFAULT_LIST.id)).thenReturn(
            true
        )
        whenever(sharedPreference.getCurrentTasksList()).thenReturn(
            NOT_DEFAULT_LIST.id
        )
        whenever(tasksRepository.getCategoryById(NOT_DEFAULT_LIST.id)).thenReturn(
            Single.just(Result.Success(NOT_DEFAULT_LIST))
        )
        whenever(tasksRepository.deleteCategory(NOT_DEFAULT_LIST)).thenReturn(
            Completable.complete()
        )

        viewModel.updateCurrentCategory(NOT_DEFAULT_LIST.id)
        viewModel.deleteCurrentCategory()

        verify(tasksRepository).deleteCategory(argWhere { it.id == NOT_DEFAULT_LIST.id })
        Truth.assertThat(viewModel.snackBarEvent.getOrAwaitValue().getContentIfNotHandled())
            .isEqualTo(R.string.category_delete_success)

        Truth.assertThat(viewModel.currentCategory.getOrAwaitValue())
            .isEqualTo(DEFAULT_TASK_LIST)
    }


}