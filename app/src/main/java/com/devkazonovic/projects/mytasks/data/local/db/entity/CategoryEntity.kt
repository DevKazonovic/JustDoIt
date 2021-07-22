package com.devkazonovic.projects.mytasks.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_list")
data class CategoryEntity(
    val name: String,
    val isDefault: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
