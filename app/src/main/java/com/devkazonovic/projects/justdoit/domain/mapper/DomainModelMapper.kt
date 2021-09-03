package com.devkazonovic.projects.justdoit.domain.mapper

import com.devkazonovic.projects.justdoit.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.Repeat
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskNotificationEntity
import com.devkazonovic.projects.justdoit.domain.model.Category
import com.devkazonovic.projects.justdoit.domain.model.Task
import com.devkazonovic.projects.justdoit.domain.model.TaskNotification
import com.devkazonovic.projects.justdoit.help.util.booleanToInt
import org.threeten.bp.Instant
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
            isDefault = booleanToInt(input.isDefault),
            createdAt = input.createdAt ?: Instant.now().toEpochMilli()
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
