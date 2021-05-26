package com.devkazonovic.projects.mytasks.domain.model

import org.threeten.bp.OffsetDateTime

data class Task(
    val id: Long = 0,
    val title: String,
    val detail: String = "",
    val isCompleted: Boolean = false,
    val listID: Long = 0,
    val date: OffsetDateTime? = null,
    val completedAt: OffsetDateTime? = null
)
