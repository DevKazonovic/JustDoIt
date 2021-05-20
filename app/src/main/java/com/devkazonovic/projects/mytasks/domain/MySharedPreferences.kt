package com.devkazonovic.projects.mytasks.domain

import android.content.Context
import android.content.Context.MODE_PRIVATE

private const val KEY_TASKS_LIST = "Current Task List"

class MySharedPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)

    fun saveCurrentTasksList(taskListID: Long) : Boolean{
        return with(preferences.edit()) {
            putLong(KEY_TASKS_LIST, taskListID)
        }.commit()
    }

    fun getCurrentTasksList(): Long = preferences.getLong(KEY_TASKS_LIST, 0)


}