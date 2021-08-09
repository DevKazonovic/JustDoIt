package com.devkazonovic.projects.mytasks.presentation.tasks.diff

import androidx.recyclerview.widget.DiffUtil
import com.devkazonovic.projects.mytasks.domain.model.Category

class CategoriesDiffCallback
    : DiffUtil.ItemCallback<Category>() {

    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}