package com.devkazonovic.projects.mytasks.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_list")
data class TaskListEntity(
    val name: String,
    val isDefault: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
