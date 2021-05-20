package com.devkazonovic.projects.mytasks

import android.app.Application
import com.devkazonovic.projects.mytasks.data.db.TasksDataBase
import com.facebook.stetho.Stetho
import timber.log.Timber

class MyTasksApplication : Application() {

    val dao by lazy {
        TasksDataBase.getInstance(this).tasksDao()
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this);
        }

    }
}