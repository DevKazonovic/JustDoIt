package com.devkazonovic.projects.mytasks.domain.model

data class Category(
    val id: Long = -1,
    val name: String,
    val isDefault: Boolean = false,
    val tasksNumber: Int = 0,
) {
    companion object {
        val DEFAULT_LIST = Category(0, "MyList", true)
    }
}