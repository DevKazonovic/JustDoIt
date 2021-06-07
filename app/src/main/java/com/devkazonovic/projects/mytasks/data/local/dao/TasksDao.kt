package com.devkazonovic.projects.mytasks.data.local.dao

import androidx.room.*
import com.devkazonovic.projects.mytasks.data.local.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.local.entities.TaskListEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface TasksDao {

    @Insert
    fun insert(task: TaskEntity): Completable

    @Update
    fun update(task: TaskEntity): Completable

    @Delete
    fun delete(task: TaskEntity): Completable

    @Insert
    fun insertAndReturnID(task: TaskEntity): Single<Long>


    @Insert
    fun insert(taskList: TaskListEntity): Single<Long>

    @Delete
    fun delete(taskList: TaskListEntity): Completable

    @Update
    fun update(taskList: TaskListEntity): Completable


    @Query("SELECT * FROM task WHERE id = :taskID")
    fun getTask(taskID: Long): Single<TaskEntity>

    @Query("SELECT * FROM task_list WHERE id = :listID")
    fun getTasksList(listID: Long): Single<TaskListEntity>

    @Query("SELECT * FROM task")
    fun getAllTasks(): Single<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE isCompleted = 1 AND listID = :listID ORDER BY datetime(completedAt) ASC")
    fun getCompletedTasks(listID: Long): Flowable<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE isCompleted = 0 AND listID = :listID ORDER BY datetime(date) DESC")
    fun getUnCompletedTasks(listID: Long): Flowable<List<TaskEntity>>

    @Query("SELECT * FROM task_list")
    fun getAllTasksLists(): Flowable<List<TaskListEntity>>

    @Query("UPDATE task SET isCompleted = :isCompleted WHERE id = :taskID")
    fun markTaskAsCompleted(taskID: Long, isCompleted: Int): Completable

    @Query("DELETE FROM task WHERE isCompleted = 1")
    fun clearCompletedTasks(): Completable

}