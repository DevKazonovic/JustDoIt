package com.devkazonovic.projects.mytasks.presentation.tasks.adapter

import androidx.recyclerview.widget.DiffUtil
import com.devkazonovic.projects.mytasks.domain.model.TaskList

class TasksListsDiffCallback
    : DiffUtil.ItemCallback<TaskList>() {

    override fun areItemsTheSame(oldItem: TaskList, newItem: TaskList): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TaskList, newItem: TaskList): Boolean {
        return oldItem == newItem
    }
}