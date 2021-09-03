package com.devkazonovic.projects.justdoit.presentation.tasks.adapter.diff

import androidx.recyclerview.widget.DiffUtil
import com.devkazonovic.projects.justdoit.presentation.tasks.model.ActiveTask

class ActiveTasksDiffCallback
    : DiffUtil.ItemCallback<ActiveTask>() {


    override fun areItemsTheSame(oldItem: ActiveTask, newItem: ActiveTask): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ActiveTask, newItem: ActiveTask): Boolean {
        return oldItem == newItem
    }
}