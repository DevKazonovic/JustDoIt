package com.devkazonovic.projects.mytasks.data.local

import com.devkazonovic.projects.mytasks.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface ILocalDataSource {

    fun insertCategory(category: CategoryEntity): Single<Long>
    fun updateCategory(category: CategoryEntity): Completable
    fun deleteCategory(category: CategoryEntity): Completable
    fun getCategoryById(listID: Long): Single<CategoryEntity>
    fun getCategories(): Flowable<List<CategoryEntity>>

    fun insertTask(task: TaskEntity): Completable
    fun updateTask(task: TaskEntity): Completable
    fun deleteTask(task: TaskEntity): Completable
    fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable
    fun getTask(taskID: Long): Single<TaskEntity>
    fun getAllTasks(): Single<List<TaskEntity>>
    fun getCompletedTasks(listID: Long): Flowable<List<TaskEntity>>
    fun getUnCompletedTasks(listID: Long): Flowable<List<TaskEntity>>
    fun markTaskAsCompleted(taskId: Long, completedAt: String): Completable
    fun markTaskAsUnCompleted(taskId: Long): Completable

    fun clearCompletedTasks(): Completable
}