package com.devkazonovic.projects.mytasks.presentation.tasks.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.devkazonovic.projects.mytasks.domain.model.Task

class TasksDiffCallback
    : DiffUtil.ItemCallback<Task>() {


    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}