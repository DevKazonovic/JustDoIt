package com.devkazonovic.projects.justdoit.data.local.source

import com.devkazonovic.projects.justdoit.data.local.db.TasksDataBase
import com.devkazonovic.projects.justdoit.data.local.db.dao.CategoryDao
import com.devkazonovic.projects.justdoit.data.local.db.dao.TaskDao
import com.devkazonovic.projects.justdoit.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    dataBase: TasksDataBase,
) : ILocalDataSource {

    private val taskDao: TaskDao = dataBase.taskDao()
    private val categoryDao: CategoryDao = dataBase.categoryDao()


    override fun insertCategory(category: CategoryEntity): Single<Long> {
        return categoryDao.insertAndReturnId(category)
    }

    override fun updateCategory(category: CategoryEntity): Completable {
        return categoryDao.update(category)
    }

    override fun deleteCategory(category: CategoryEntity): Completable {
        return categoryDao.delete(category)
    }

    override fun getCategoryById(listID: Long): Single<CategoryEntity> {
        return categoryDao.getCategoryById(listID)
    }

    override fun getCategories(): Flowable<List<CategoryEntity>> {
        return categoryDao.getCategories()
    }

    override fun insertTask(task: TaskEntity): Completable =
        taskDao.insert(task)


    override fun insertTaskAndReturn(task: TaskEntity): Single<Long> =
        taskDao.insertAndReturnId(task)


    override fun updateTask(task: TaskEntity): Completable {
        return taskDao.update(task)
    }

    override fun deleteTask(task: TaskEntity): Completable {
        return taskDao.delete(task)
    }

    override fun deleteTaskById(taskID: Long): Completable {
        return taskDao.deleteTaskById(taskID)
    }

    override fun deleteTasks(tasks: List<Long>): Completable {
        return try {
            taskDao.deleteTasks(tasks)
            Completable.complete()
        } catch (e: Exception) {
            Completable.error(Exception(""))
        }
    }

    override fun updateTaskReminder(taskID: Long, reminderDate: Long?): Completable {
        return taskDao.updateTaskDueDate(taskID, reminderDate)
    }

    override fun updateTaskNextAlarm(taskID: Long, repeatNextDueDate: Long?): Completable {
        return taskDao.updateTaskNextAlarm(taskID, repeatNextDueDate)
    }

    override fun getTask(taskID: Long): Single<TaskEntity> {
        return taskDao.getTask(taskID)
    }

    override fun getAllTasks(): Single<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }

    override fun getCategoryTasks(listID: Long): Flowable<List<TaskEntity>> {
        return taskDao.getCategoryTasks(listID)
    }

    override fun getCompletedTasks(listID: Long): Flowable<List<TaskEntity>> {
        return taskDao.getCompletedTasks(listID)
    }

    override fun getUnCompletedTasks(listID: Long): Flowable<List<TaskEntity>> {
        return taskDao.getUnCompletedTasks(listID)
    }

    override fun markTaskAsCompleted(taskId: Long, completedAt: String): Completable {
        return taskDao.markTaskAsCompleted(taskId, completedAt)
    }

    override fun markTaskAsUnCompleted(taskId: Long): Completable {
        return taskDao.markTaskAsUnCompleted(taskId)
    }

    override fun clearCompletedTasks(): Completable {
        return taskDao.clearCompletedTasks()
    }
}