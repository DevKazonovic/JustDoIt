package com.devkazonovic.projects.mytasks.data.local.preference

import android.content.Context
import android.content.Context.MODE_PRIVATE
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val KEY_TASKS_LIST = "Current Task List"
private const val KEY_REQUEST_CODE = "Current Pending Intent Request Code"

class MySharedPreferences @Inject constructor(
    @ApplicationContext context: Context,
) : IMainSharedPreference {

    private val preferences =
        context.getSharedPreferences(context.packageName, MODE_PRIVATE)

    override fun saveCurrentTasksList(taskListID: Long): Boolean {
        return with(preferences.edit()) {
            putLong(KEY_TASKS_LIST, taskListID)
        }.commit()
    }

    override fun getCurrentTasksList(): Long = preferences.getLong(KEY_TASKS_LIST, 0)

    override fun saveRequestCode(requestCode: Int): Boolean {
        return with(preferences.edit()) {
            putInt(KEY_REQUEST_CODE, requestCode)
        }.commit()
    }

    override fun getCurrentRequestCode(): Int = preferences.getInt(KEY_REQUEST_CODE, -1)


}