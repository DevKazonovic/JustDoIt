package com.devkazonovic.projects.mytasks.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "task")
data class TaskEntity(
    val title: String,
    val detail: String = "",
    val isCompleted: Int = 0,
    val listID: Long = 0,
    val date: OffsetDateTime? = null,
    val completedAt: OffsetDateTime? = null

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
