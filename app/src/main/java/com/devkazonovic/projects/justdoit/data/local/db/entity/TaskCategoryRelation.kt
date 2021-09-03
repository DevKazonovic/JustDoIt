package com.devkazonovic.projects.justdoit.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TaskCategoryRelation(
    @Embedded val list: CategoryEntity,
    @Relation(parentColumn = "id", entityColumn = "categoryId")
    val tasks: List<TaskEntity>,
)
