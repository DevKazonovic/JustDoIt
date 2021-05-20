package com.devkazonovic.projects.mytasks.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val detail: String = "",
    val isCompleted: Boolean = false,
    val listID: Long = 0
)
