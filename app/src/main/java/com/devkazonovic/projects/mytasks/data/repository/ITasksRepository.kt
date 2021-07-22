package com.devkazonovic.projects.mytasks.data.repository

import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.holder.Result
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import org.threeten.bp.OffsetDateTime

interface ITasksRepository {

    fun addNewCategory(category: Category): Single<Result<Long>>
    fun updateCategory(category: Category): Completable
    fun deleteCategory(category: Category): Completable
    fun getCategoryById(listID: Long): Single<Result<Category>>
    fun getCategories(): Flowable<Result<List<Category>>>

    fun addNewTask(task: Task): Completable
    fun updateTask(task: Task): Completable
    fun deleteTask(task: Task): Completable
    fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable
    fun getTask(taskID: Long): Single<Result<Task>>
    fun getAllTasks(): Single<Result<List<Task>>>
    fun getCompletedTasks(listID: Long): Flowable<Result<List<Task>>>
    fun getUnCompletedTasks(listID: Long): Flowable<Result<List<Task>>>
    fun markTaskAsCompleted(taskId: Long, completedAt: OffsetDateTime): Completable
    fun markTaskAsUnCompleted(taskId: Long): Completable
    fun clearCompletedTasks(): Completable
}