package com.devkazonovic.projects.mytasks.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "task")
data class TaskEntity(
    val title: String,
    val detail: String = "",
    val isCompleted: Int = 0,
    val listID: Long = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
