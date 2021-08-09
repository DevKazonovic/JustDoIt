package com.devkazonovic.projects.mytasks.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "task")
data class TaskEntity(
    val categoryId: Long = 0,
    val title: String,
    val detail: String = "",
    val isCompleted: Int = 0,
    val createdAt: OffsetDateTime? = null,
    val completedAt: OffsetDateTime? = null,
    val alarmId: Int? = null,
    val dueDateInMillis: Long? = null,
    val isAllDay: Int = 1,
    @Embedded val repeat: Repeat? = null,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
