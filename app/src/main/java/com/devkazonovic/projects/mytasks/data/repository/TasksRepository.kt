package com.devkazonovic.projects.mytasks.data.repository

import com.devkazonovic.projects.mytasks.data.local.ILocalDataSource
import com.devkazonovic.projects.mytasks.domain.holder.Result
import com.devkazonovic.projects.mytasks.domain.mapper.IMappers
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.util.offsetDateTimeToString
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.OffsetDateTime
import javax.inject.Inject

class TasksRepository @Inject constructor(
    private val localDataSource: ILocalDataSource,
    private val mappers: IMappers
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

    override fun addNewTask(task: Task): Completable =
        localDataSource.insertTask(mappers.taskMapper().map(task))


    override fun updateTask(task: Task): Completable =
        localDataSource.updateTask(mappers.taskMapper().map(task))


    override fun deleteTask(task: Task): Completable =
        localDataSource.deleteTask(mappers.taskMapper().map(task))

    override fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable =
        localDataSource.updateTaskReminder(taskID, reminderDate)

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