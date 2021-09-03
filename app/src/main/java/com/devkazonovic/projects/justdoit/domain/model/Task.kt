package com.devkazonovic.projects.justdoit.domain.model

import org.threeten.bp.OffsetDateTime

data class Task(
    val id: Long = -1,
    val title: String,
    val detail: String = "",
    val isCompleted: Boolean = false,
    val categoryId: Long = 0,
    val date: OffsetDateTime? = null,
    val completedAt: OffsetDateTime? = null,
    val dueDate: Long? = null,
    val isAllDay: Boolean = true,
    val alarmId: Int? = null,
    val repeatType: RepeatType? = null,
    val repeatValue: Int? = null,
    val nextDueDate: Long? = null,
)
