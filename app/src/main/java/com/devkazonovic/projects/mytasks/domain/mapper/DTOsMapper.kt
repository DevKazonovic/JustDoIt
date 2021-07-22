package com.devkazonovic.projects.mytasks.domain.mapper

import com.devkazonovic.projects.mytasks.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskEntity
import com.devkazonovic.projects.mytasks.domain.model.Category
import com.devkazonovic.projects.mytasks.domain.model.Task
import com.devkazonovic.projects.mytasks.help.util.htmlToString
import javax.inject.Inject

class TaskEntityMapper @Inject constructor() : Mapper<TaskEntity, Task> {
    override fun map(input: TaskEntity): Task =
        Task(
            id = input.id,
            title = htmlToString(input.title),
            detail = htmlToString(input.detail),
            isCompleted = input.isCompleted == 1,
            listID = input.listID,
            date = input.createdAt,
            completedAt = input.completedAt,
            reminderDate = input.reminderDate,
            pendingIntentRequestCode = input.pendingIntentRequestCode
        )
}

class CategoryEntityMapper @Inject constructor() : Mapper<CategoryEntity, Category> {
    override fun map(input: CategoryEntity): Category =
        Category(
            id = input.id,
            name = htmlToString(input.name),
            isDefault = input.isDefault == 1
        )
}