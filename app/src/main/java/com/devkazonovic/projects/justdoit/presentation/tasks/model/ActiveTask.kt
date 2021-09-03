package com.devkazonovic.projects.justdoit.presentation.tasks.model

import com.devkazonovic.projects.justdoit.domain.model.Task

sealed class ActiveTask {
    abstract val id: Long

    data class ItemTask(val task: Task) : ActiveTask() {
        override val id: Long
            get() = task.id
    }

    data class ItemHeader(val type: HeaderType) : ActiveTask() {
        override val id: Long
            get() = Long.MIN_VALUE
    }

    enum class HeaderType {
        OVERDUE, NO_DATE, ACTIVE
    }
}

