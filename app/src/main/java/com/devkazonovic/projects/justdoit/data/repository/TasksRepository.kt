package com.devkazonovic.projects.justdoit.data.repository

import com.devkazonovic.projects.justdoit.data.local.preference.IMainSharedPreference
import com.devkazonovic.projects.justdoit.data.local.source.ILocalDataSource
import com.devkazonovic.projects.justdoit.domain.holder.Result
import com.devkazonovic.projects.justdoit.domain.mapper.IMappers
import com.devkazonovic.projects.justdoit.domain.model.Category
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.help.util.log
import com.devkazonovic.projects.justdoit.help.util.offsetDateTimeToString
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val localDataSource: ILocalDataSource,
    private val mappers: IMappers,
    private val sharedPreference: IMainSharedPreference,
) : ITasksRepository {

    override fun addNewCategory(category: Category): Single<Result<Long>> =
        localDataSource
            .insertCategory(mappers.categoryMapper().map(category))
            .toResult()

    override fun updateCategory(category: Category): Completable =
        localDataSource.updateCategory(mappers.categoryMapper().map(category))

    override fun deleteCategory(category: Category): Completable =
        localDataSource.deleteCategory(mappers.categoryMapper().map(category))

    override fun getCategoryById(listID: Long): Single<Result<Category>> =
        localDataSource.getCategoryById(listID)
            .map { mappers.categoryEntityMapper().map(it) }
            .toResult()

    override fun getCategories(): Flowable<Result<List<Category>>> =
        localDataSource.getCategories()
            .map { list ->
                list.map { mappers.categoryEntityMapper().map(it) }
            }
            .toResult()

    override fun addNewTask(task: Task): Completable {
        val requestCode = sharedPreference.getCurrentRequestCode()
        val newRequestCode = requestCode + 1
        return if (sharedPreference.saveRequestCode(newRequestCode)) {
            localDataSource.insertTask(mappers.taskMapper()
                .map(task.copy(alarmId = newRequestCode)))
        } else Completable.error(Exception("We can't save Task, Please Try Again"))
    }

    override fun addNewTaskAndReturn(task: Task): Single<Result<Long>> {
        val requestCode = sharedPreference.getCurrentRequestCode()
        val newRequestCode = requestCode + 1
        val requestCodeSaved = sharedPreference.saveRequestCode(newRequestCode)
        log("requestCode : $requestCodeSaved")
        return localDataSource.insertTaskAndReturn(
            mappers.taskMapper().map(task.copy(alarmId = newRequestCode))
        ).toResult()
    }

    override fun updateTask(task: Task): Completable =
        localDataSource.updateTask(mappers.taskMapper().map(task))

    override fun deleteTask(task: Task): Completable =
        localDataSource.deleteTask(mappers.taskMapper().map(task))

    override fun deleteTaskById(taskID: Long): Completable =
        localDataSource.deleteTaskById(taskID)

    override fun deleteTasks(tasks: List<Long>): Completable =
        localDataSource.deleteTasks(tasks)

    override fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable =
        localDataSource.updateTaskReminder(taskID, reminderDate)

    override fun updateTaskNextAlarm(taskID: Long, repeatNextDueDate: Long?): Completable {
        return localDataSource.updateTaskNextAlarm(taskID, repeatNextDueDate)
    }

    override fun getTask(taskID: Long): Single<Result<Task>> =
        localDataSource.getTask(taskID)
            .map { mappers.taskEntityMapper().map(it) }
            .toResult()

    override fun getAllTasks(): Single<Result<List<Task>>> =
        localDataSource.getAllTasks()
            .map { list ->
                list.map { mappers.taskEntityMapper().map(it) }
            }
            .toResult()

    override fun getCategoryTasks(listID: Long): Flowable<Result<List<Task>>> =
        localDataSource.getCategoryTasks(listID)
            .map { list ->
                list.map { mappers.taskEntityMapper().map(it) }
            }
            .toResult()

    override fun getCompletedTasks(listID: Long): Flowable<Result<List<Task>>> =
        localDataSource.getCompletedTasks(listID)
            .map { list ->
                list.map { mappers.taskEntityMapper().map(it) }
            }
            .toResult()

    override fun getUnCompletedTasks(listID: Long): Flowable<Result<List<Task>>> =
        localDataSource.getUnCompletedTasks(listID)
            .map { list ->
                list.map { mappers.taskEntityMapper().map(it) }
            }
            .toResult()

    override fun markTaskAsCompleted(taskId: Long, completedAt: OffsetDateTime): Completable =
        localDataSource.markTaskAsCompleted(taskId, offsetDateTimeToString(completedAt) ?: "")

    override fun markTaskAsUnCompleted(taskId: Long): Completable =
        localDataSource.markTaskAsUnCompleted(taskId)

    override fun clearCompletedTasks(): Completable =
        localDataSource.clearCompletedTasks()

    private fun <T> Single<T>.toResult(): Single<Result<T>> = this
        .map<Result<T>> { Result.Success(it) }
        .onErrorReturn { Result.Failure(it) }

    private fun <T> Flowable<T>.toResult(): Flowable<Result<T>> = this
        .map<Result<T>> { Result.Success(it) }
        .onErrorReturn { Result.Failure(it) }

}