package com.devkazonovic.projects.justdoit.domain.model

data class Category(
    val id: Long = -1,
    val name: String,
    val isDefault: Boolean = false,
    val tasksNumber: Int = 0,
    val createdAt: Long? = null,
) {
    companion object {
        val DEFAULT_LIST = Category(0, "MyList", true)
    }
}