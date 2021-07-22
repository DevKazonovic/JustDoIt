package com.devkazonovic.projects.mytasks.data.local.preference

interface IMainSharedPreference {

    fun getCurrentTasksList(): Long
    fun saveCurrentTasksList(taskListID: Long): Boolean

    fun getCurrentRequestCode(): Int
    fun saveRequestCode(requestCode: Int): Boolean
}