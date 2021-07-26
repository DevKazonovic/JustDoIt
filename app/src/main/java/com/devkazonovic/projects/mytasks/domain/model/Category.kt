package com.devkazonovic.projects.mytasks.domain.model

data class Category(
    val id: Long,
    val name: String,
    val isDefault: Boolean = false
) {
    companion object {
        val DEFAULT_LIST = Category(0, "MyList", true)
    }
}