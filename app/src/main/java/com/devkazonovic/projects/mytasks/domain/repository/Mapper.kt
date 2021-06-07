package com.devkazonovic.projects.mytasks.domain.repository

import com.devkazonovic.projects.mytasks.data.local.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.local.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.booleanToInt
import com.devkazonovic.projects.mytasks.domain.htmlToString
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList

fun TaskEntity.mapToDomainModel(): Task {
    return Task(
        id = id,
        title = htmlToString(title),
        detail = htmlToString(detail),
        isCompleted = isCompleted == 1,
        listID = listID,
        date = date,
        completedAt = completedAt
    )
}

fun Task.mapToEntity(): TaskEntity {
    return TaskEntity(
        title = title,
        detail = detail,
        isCompleted = booleanToInt(isCompleted),
        listID = listID,
        date = date,
        completedAt = completedAt
    ).also { taskEntity -> taskEntity.id = id }
}

fun TaskListEntity.mapToDomainModel(): TaskList {
    return TaskList(
        id = id,
        name = htmlToString(name),
        isDefault = isDefault == 1
    )
}

fun TaskList.mapToEntity(): TaskListEntity {
    return TaskListEntity(
        name = name,
        isDefault = booleanToInt(isDefault)
    ).also { it.id = id }
}

