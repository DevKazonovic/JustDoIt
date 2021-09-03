package com.devkazonovic.projects.justdoit.data.local.db.entity

data class Repeat(
    val repeatType: String? = null,
    val repeatValue: Int? = null,
    val nextDueDate: Long? = null,
)