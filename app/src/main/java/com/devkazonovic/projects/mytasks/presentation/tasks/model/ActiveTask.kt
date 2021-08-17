package com.devkazonovic.projects.mytasks.presentation.tasks.model

import com.devkazonovic.projects.mytasks.domain.model.Task

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

