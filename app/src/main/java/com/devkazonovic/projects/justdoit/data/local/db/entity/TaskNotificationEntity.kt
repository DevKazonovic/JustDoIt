package com.devkazonovic.projects.justdoit.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_notification")
data class TaskNotificationEntity(
    @PrimaryKey
    val id: Int,
    val state: String,
)