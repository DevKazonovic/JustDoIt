package com.devkazonovic.projects.mytasks.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
data class CategoryEntity(
    val name: String,
    val isDefault: Int = 0,
    val createdAt: Long,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
