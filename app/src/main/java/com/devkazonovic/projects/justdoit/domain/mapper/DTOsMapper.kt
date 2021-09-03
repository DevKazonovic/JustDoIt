package com.devkazonovic.projects.justdoit.domain.mapper

import com.devkazonovic.projects.justdoit.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskNotificationEntity
import com.devkazonovic.projects.justdoit.domain.model.*
import com.devkazonovic.projects.justdoit.help.util.htmlToString
import javax.inject.Inject

class TaskEntityMapper @Inject constructor() : Mapper<TaskEntity, Task> {
    override fun map(input: TaskEntity): Task =
        Task(
            id = input.id,
            title = htmlToString(input.title),
            detail = htmlToString(input.detail),
            isCompleted = input.isCompleted == 1,
            categoryId = input.categoryId,
            date = input.createdAt,
            completedAt = input.completedAt,
            dueDate = input.dueDateInMillis,
            isAllDay = input.isAllDay == 1,
            alarmId = input.alarmId,
            repeatType = input.repeat?.repeatType?.let { RepeatType.valueOf(it) },
            repeatValue = input.repeat?.repeatValue,
            nextDueDate = input.repeat?.nextDueDate
        )
}

class CategoryEntityMapper @Inject constructor() : Mapper<CategoryEntity, Category> {
    override fun map(input: CategoryEntity): Category =
        Category(
            id = input.id,
            name = htmlToString(input.name),
            isDefault = input.isDefault == 1,
            createdAt = input.createdAt
        )
}

class TaskNotificationEntityMapper @Inject constructor() :
    Mapper<TaskNotificationEntity, TaskNotification> {
    override fun map(input: TaskNotificationEntity): TaskNotification {
        return TaskNotification(
            input.id,
            TaskNotificationState.valueOf(input.state)
        )
    }
}
