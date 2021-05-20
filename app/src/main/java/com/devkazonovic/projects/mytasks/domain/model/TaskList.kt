package com.devkazonovic.projects.mytasks.domain.model

data class TaskList(
    val id: Long,
    val name: String,
    val isDefault: Boolean = false
)
