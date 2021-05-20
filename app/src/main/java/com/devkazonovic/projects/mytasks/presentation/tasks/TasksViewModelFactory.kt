package com.devkazonovic.projects.mytasks.presentation.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devkazonovic.projects.mytasks.domain.TasksRepository

class TasksViewModelFactory(private val tasksRepository: TasksRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            return TasksViewModel(tasksRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}