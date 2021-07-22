package com.devkazonovic.projects.mytasks.fake

import com.devkazonovic.projects.mytasks.data.local.preference.IMainSharedPreference

class FakeSharedPreference : IMainSharedPreference {

    private var currentListID: Long = 0

    override fun getCurrentTasksList(): Long = currentListID

    override fun saveCurrentTasksList(taskListID: Long): Boolean {
        currentListID = taskListID
        return true
    }

    override fun getCurrentRequestCode(): Int {
        TODO("Not yet implemented")
    }

    override fun saveRequestCode(requestCode: Int): Boolean {
        TODO("Not yet implemented")
    }
}