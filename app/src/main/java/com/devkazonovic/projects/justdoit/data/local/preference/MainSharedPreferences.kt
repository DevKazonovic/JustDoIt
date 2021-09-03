package com.devkazonovic.projects.justdoit.data.local.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.devkazonovic.projects.justdoit.presentation.categories.CategorySort
import com.devkazonovic.projects.justdoit.presentation.common.model.SortDirection
import com.devkazonovic.projects.justdoit.presentation.tasks.model.TasksSort
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val KEY_TASKS_LIST = "Current Task Category"
private const val KEY_REQUEST_CODE = "Current Pending Intent Request Code"
private const val KEY_CATEGORIES_SORT = "Categories Sort"
private const val KEY_CATEGORIES_SORT_ORDER = "Categories Sort direction"
private const val KEY_TASKS_SORT = "Tasks Sort"
private const val KEY_TASKS_SORT_ORDER = "Tasks Sort Order"

class MySharedPreferences @Inject constructor(
    @ApplicationContext context: Context,
) : IMainSharedPreference {

    private val preferences =
        context.getSharedPreferences(context.packageName, MODE_PRIVATE)

    override fun saveCurrentCategory(taskListID: Long): Boolean {
        return with(preferences.edit()) {
            putLong(KEY_TASKS_LIST, taskListID)
        }.commit()
    }

    override fun saveRequestCode(requestCode: Int): Boolean {
        return with(preferences.edit()) {
            putInt(KEY_REQUEST_CODE, requestCode)
        }.commit()
    }

    override fun saveCategoriesSort(sort: String): Boolean {
        return with(preferences.edit()) {
            putString(KEY_CATEGORIES_SORT, sort)
        }.commit()
    }

    override fun saveCategoriesSortOrder(order: String): Boolean {
        return with(preferences.edit()) {
            putString(KEY_CATEGORIES_SORT_ORDER, order)
        }.commit()
    }

    override fun saveTasksSort(sort: String): Boolean {
        return with(preferences.edit()) {
            putString(KEY_TASKS_SORT, sort)
        }.commit()
    }

    override fun saveTasksSortOrder(order: String): Boolean {
        return with(preferences.edit()) {
            putString(KEY_TASKS_SORT_ORDER, order)
        }.commit()
    }

    override fun getCurrentCategory(): Long = preferences.getLong(KEY_TASKS_LIST, 0)

    override fun getCurrentRequestCode(): Int = preferences.getInt(KEY_REQUEST_CODE, -1)

    override fun getCategoriesSort(): String? = preferences.getString(
        KEY_CATEGORIES_SORT, CategorySort.DEFAULT.name
    )

    override fun getCategoriesSortOrder(): String? = preferences.getString(
        KEY_CATEGORIES_SORT_ORDER,
        SortDirection.ASC.name
    )

    override fun getTasksSort(): String? = preferences.getString(
        KEY_TASKS_SORT,
        TasksSort.DEFAULT.name
    )

    override fun getTasksSortOrder(): String? = preferences.getString(
        KEY_TASKS_SORT_ORDER,
        SortDirection.ASC.name
    )


}