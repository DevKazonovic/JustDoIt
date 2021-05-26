package com.devkazonovic.projects.mytasks.data

import com.devkazonovic.projects.mytasks.data.db.dao.TasksDao
import com.devkazonovic.projects.mytasks.data.db.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.db.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.TasksRepository
import com.devkazonovic.projects.mytasks.domain.mapToDomainModel
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class TasksRepositoryImpl(private val tasksDao: TasksDao) : TasksRepository {

    override fun insert(task: TaskEntity): Completable {
        return tasksDao.insert(task)
    }

    override fun update(task: TaskEntity): Completable {
        return tasksDao.update(task)
    }

    override fun delete(task: TaskEntity): Completable {
        return tasksDao.delete(task)
    }


    override fun insert(taskList: TaskListEntity): Single<Long> {
        return tasksDao.insert(taskList)
    }

    override fun update(taskList: TaskListEntity): Completable {
        return tasksDao.update(taskList)
    }

    override fun delete(taskList: TaskListEntity): Completable {
        return tasksDao.delete(taskList)
    }


    override fun getTask(taskID: Long): Single<Task> {
        return tasksDao.getTask(taskID).map { it.mapToDomainModel() }
    }

    override fun getTasksList(listID: Long): Single<TaskList> {
        return tasksDao.getTasksList(listID).map { it.mapToDomainModel() }
    }

    override fun getAllTasks(): Single<List<Task>> {
        return tasksDao.getAllTasks().map { it.map { task -> task.mapToDomainModel() } }
    }

    override fun getCompletedTasks(listID: Long): Flowable<List<Task>> {
        return tasksDao.getCompletedTasks(listID).map { it.map { task -> task.mapToDomainModel() } }
    }

    override fun getUnCompletedTasks(listID: Long): Flowable<List<Task>> {
        return tasksDao.getUnCompletedTasks(listID)
            .map { it.map { task -> task.mapToDomainModel() } }
    }

    override fun getAllTasksLists(): Flowable<List<TaskList>> {
        return tasksDao.getAllTasksLists().map { it.map { list -> list.mapToDomainModel() } }
    }

    override fun markTaskAsCompleted(
        task: TaskEntity
    ): Completable {
        return tasksDao.update(task)
    }

    override fun clearCompletedTasks(): Completable {
        return tasksDao.clearCompletedTasks()
    }
}