package com.devkazonovic.projects.mytasks.domain.mapper

import com.devkazonovic.projects.mytasks.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.mytasks.data.local.db.entity.Repeat
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskEntity
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskNotificationEntity
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.domain.model.TaskNotification
import com.devkazonovic.projects.mytasks.help.util.booleanToInt
import javax.inject.Inject

class TaskMapper @Inject constructor() : Mapper<Task, TaskEntity> {
    override fun map(input: Task): TaskEntity =
        TaskEntity(
            title = input.title,
            detail = input.detail,
            isCompleted = booleanToInt(input.isCompleted),
            categoryId = input.categoryId,
            createdAt = input.date,
            completedAt = input.completedAt,
            dueDateInMillis = input.dueDate,
            isAllDay = booleanToInt(input.isAllDay),
            alarmId = input.alarmId,
            repeat = Repeat(input.repeatType?.name, input.repeatValue, input.nextDueDate)
        ).also { taskEntity ->
            taskEntity.id = input.id
        }
}

class CategoryMapper @Inject constructor() : Mapper<Category, CategoryEntity> {
    override fun map(input: Category): CategoryEntity =
        CategoryEntity(
            name = input.name,
            isDefault = booleanToInt(input.isDefault)
        ).also { category -> if (input.id != -1L) category.id = input.id }
}

class TaskNotificationMapper @Inject constructor() :
    Mapper<TaskNotification, TaskNotificationEntity> {
    override fun map(input: TaskNotification): TaskNotificationEntity {
        return TaskNotificationEntity(
            input.id,
            input.state.name
        )
    }
}
