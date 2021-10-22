package com.devkazonovic.projects.justdoit.data.local.db.dao

import androidx.room.Dao
import androidx.room.Query
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
abstract class TaskDao : BaseCrudDao<TaskEntity> {

    @Query("DELETE FROM task WHERE id = :taskID")
    abstract fun deleteTaskById(taskID: Long): Completable


    fun deleteTasks(tasks: List<Long>) {
        tasks.forEach { deleteTaskById(it) }
    }

    @Query("UPDATE task SET dueDateInMillis = :reminderDate WHERE id = :taskID")
    abstract fun updateTaskDueDate(taskID: Long, reminderDate: Long?): Completable

    @Query("UPDATE task SET nextDueDate = :repeatNextDueDate WHERE id = :taskID")
    abstract fun updateTaskNextAlarm(taskID: Long, repeatNextDueDate: Long?): Completable

    @Query("SELECT * FROM task WHERE id = :taskID")
    abstract fun getTask(taskID: Long): Single<TaskEntity>

    @Query("SELECT * FROM task")
    abstract fun getAllTasks(): Single<List<TaskEntity>>

    @Query("SELECT * FROM task where categoryId = :listID")
    abstract fun getCategoryTasks(listID: Long): Flowable<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE isCompleted = 1 AND categoryId = :listID ORDER BY datetime(completedAt) ASC")
    abstract fun getCompletedTasks(listID: Long): Flowable<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE isCompleted = 0 AND categoryId = :listID ORDER BY datetime(createdAt) DESC")
    abstract fun getUnCompletedTasks(listID: Long): Flowable<List<TaskEntity>>

    @Query("UPDATE task SET isCompleted = 1,completedAt = :completedAt WHERE id = :taskID")
    abstract fun markTaskAsCompleted(taskID: Long, completedAt: String): Completable

    @Query("UPDATE task SET isCompleted = 0,completedAt = null WHERE id = :taskID")
    abstract fun markTaskAsUnCompleted(taskID: Long): Completable

    @Query("DELETE FROM task WHERE isCompleted = 1")
    abstract fun clearCompletedTasks(): Completable

}