package com.devkazonovic.projects.mytasks.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TaskCategoryRelation(
    @Embedded val list: CategoryEntity,
    @Relation(parentColumn = "id", entityColumn = "listID")
    val tasks: List<TaskEntity>
)
