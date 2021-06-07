package com.devkazonovic.projects.mytasks.domain.repository

import com.devkazonovic.projects.mytasks.data.local.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.local.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

interface TasksRepository {

    fun insert(taskList: TaskListEntity): Single<Long>
    fun update(taskList: TaskListEntity): Completable
    fun delete(taskList: TaskListEntity): Completable

    fun insert(task: TaskEntity): Completable
    fun update(task: TaskEntity): Completable
    fun delete(task: TaskEntity): Completable

    fun getTask(taskID: Long): Single<Task>
    fun getAllTasks(): Single<List<Task>>
    fun getCompletedTasks(listID: Long): Flowable<List<Task>>
    fun getUnCompletedTasks(listID: Long): Flowable<List<Task>>
    fun getAllLists(): Flowable<List<TaskList>>

    fun markTaskAsCompleted(task: TaskEntity): Completable

    fun clearCompletedTasks(): Completable
    fun getTasksList(listID: Long): Single<TaskList>
}