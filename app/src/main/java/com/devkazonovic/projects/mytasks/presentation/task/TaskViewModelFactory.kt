package com.devkazonovic.projects.mytasks.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.devkazonovic.projects.mytasks.domain.TasksRepository

class TaskViewModelFactory(private val tasksRepository: TasksRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(tasksRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}