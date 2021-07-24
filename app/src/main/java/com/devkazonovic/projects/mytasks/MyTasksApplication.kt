package com.devkazonovic.projects.mytasks

import android.app.Application
import com.devkazonovic.projects.mytasks.service.TaskNotificationManager
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyTasksApplication : Application() {

    @Inject
    lateinit var taskNotificationManager: TaskNotificationManager

    companion object {
        var myPackageName: String = "DEFAULT_PACKAGE"
    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Stetho.initializeWithDefaults(this)
        }
        myPackageName = packageName
        taskNotificationManager.createNotificationChannel()
    }
}