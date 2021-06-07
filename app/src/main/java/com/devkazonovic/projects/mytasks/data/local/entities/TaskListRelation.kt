package com.devkazonovic.projects.mytasks.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class TaskListRelation(
    @Embedded val list: TaskListEntity,
    @Relation(parentColumn = "id", entityColumn = "listID")
    val tasks: List<TaskEntity>
)
