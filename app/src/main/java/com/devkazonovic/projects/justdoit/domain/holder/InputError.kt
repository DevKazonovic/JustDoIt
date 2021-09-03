package com.devkazonovic.projects.justdoit.domain.holder

sealed class InputError {
    data class TaskTitleInput(val message: Int) : InputError()
    data class TaskDetailInput(val message: Int) : InputError()
    data class CategoryNameInput(val message: Int) : InputError()

}
