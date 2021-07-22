package com.devkazonovic.projects.mytasks.domain.model

data class Category(
    val id: Long,
    val name: String,
    val isDefault: Boolean = false
)