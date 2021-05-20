package com.devkazonovic.projects.mytasks.domain

import com.devkazonovic.projects.mytasks.data.db.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.db.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskList

fun TaskEntity.mapToDomainModel(): Task {
    return Task(
        id = id,
        title = htmlToString(title),
        detail = htmlToString(detail),
        isCompleted = isCompleted == 1,
        listID = listID
    )
}

fun Task.mapToEntity(): TaskEntity {
    return TaskEntity(
        title = title,
        detail = detail,
        booleanToInt(isCompleted),
        listID = listID
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

