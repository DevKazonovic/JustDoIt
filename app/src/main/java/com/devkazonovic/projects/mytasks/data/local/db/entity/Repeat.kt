package com.devkazonovic.projects.mytasks.data.local.db.entity

data class Repeat(
    val repeatType: String? = null,
    val repeatValue: Int? = null,
    val nextDueDate: Long? = null,
)