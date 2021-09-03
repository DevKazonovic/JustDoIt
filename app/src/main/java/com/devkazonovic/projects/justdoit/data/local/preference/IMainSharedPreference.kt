package com.devkazonovic.projects.justdoit.data.local.preference

interface IMainSharedPreference {

    fun getCurrentCategory(): Long
    fun saveCurrentCategory(taskListID: Long): Boolean

    fun getCurrentRequestCode(): Int
    fun saveRequestCode(requestCode: Int): Boolean

    fun getCategoriesSort(): String?
    fun saveCategoriesSort(sort: String): Boolean

    fun getCategoriesSortOrder(): String?
    fun saveCategoriesSortOrder(order: String): Boolean

    fun getTasksSort(): String?
    fun saveTasksSort(sort: String): Boolean

    fun getTasksSortOrder(): String?
    fun saveTasksSortOrder(order: String): Boolean
}