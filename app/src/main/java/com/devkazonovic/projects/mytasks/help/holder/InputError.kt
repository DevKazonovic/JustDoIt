package com.devkazonovic.projects.mytasks.help.holder

sealed class InputError {
    data class TaskTitleInput(val message: Int) : InputError()
    data class TaskDetailInput(val message: Int) : InputError()

}
