package com.devkazonovic.projects.mytasks.presentation.tasks.diff

import androidx.recyclerview.widget.DiffUtil
import com.devkazonovic.projects.mytasks.presentation.tasks.adapter.ActiveTask

class ActiveTasksDiffCallback
    : DiffUtil.ItemCallback<ActiveTask>() {


    override fun areItemsTheSame(oldItem: ActiveTask, newItem: ActiveTask): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ActiveTask, newItem: ActiveTask): Boolean {
        return oldItem == newItem
    }
}