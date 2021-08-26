package com.devkazonovic.projects.mytasks.fake

import com.devkazonovic.projects.mytasks.data.local.preference.IMainSharedPreference

class FakeSharedPreference : IMainSharedPreference {

    private var currentListID: Long = 0

    override fun getCurrentCategory(): Long = currentListID

    override fun saveCurrentCategory(taskListID: Long): Boolean {
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