package com.devkazonovic.projects.mytasks.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime


@Entity(tableName = "task")
data class TaskEntity(
    val title: String,
    val detail: String = "",
    val isCompleted: Int = 0,
    val listID: Long = 0,
    val createdAt: OffsetDateTime? = null,
    val completedAt: OffsetDateTime? = null,
    val reminderDate: Long? = null,
    val pendingIntentRequestCode: Int? = null,

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0


}
