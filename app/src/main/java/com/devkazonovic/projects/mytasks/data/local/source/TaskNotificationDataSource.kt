package com.devkazonovic.projects.mytasks.data.local.source

import com.devkazonovic.projects.mytasks.data.local.db.TasksDataBase
import com.devkazonovic.projects.mytasks.domain.mapper.Mappers
import com.devkazonovic.projects.mytasks.domain.model.TaskNotification
import com.devkazonovic.projects.mytasks.help.util.log
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject


class TaskNotificationDataSource @Inject constructor(
    mappers: Mappers,
    dataBase: TasksDataBase,
) {

    private val taskNotificationDao = dataBase.taskNotificationDao()
    private val taskNotificationMapper = mappers.taskNotificationMapper()
    private val taskNotificationEntityMapper = mappers.taskNotificationEntityMapper()

    fun insertNotification(taskNotification: TaskNotification): Completable {
        return taskNotificationDao.insert(
            taskNotificationMapper.map(taskNotification)
        )
    }

    fun updateNotification(taskNotification: TaskNotification): Completable {
        log("Update : ${taskNotificationMapper.map(taskNotification)}")
        return taskNotificationDao.updateState(taskNotification.id, taskNotification.state.name)
    }

    fun deleteNotification(id: Int): Completable {
        return taskNotificationDao.deleteById(id)
    }

    fun findNotification(id: Int): Single<TaskNotification> {
        return taskNotificationDao.getTaskNotificationById(
            id
        ).map { taskNotificationEntityMapper.map(it) }
    }
}