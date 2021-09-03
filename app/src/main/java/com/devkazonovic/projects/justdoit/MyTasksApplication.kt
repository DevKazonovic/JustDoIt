package com.devkazonovic.projects.justdoit

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.devkazonovic.projects.justdoit.data.local.preference.ISettingSharedPreference
import com.devkazonovic.projects.justdoit.domain.model.ThemeType
import com.devkazonovic.projects.justdoit.service.TaskNotificationManager
import com.facebook.stetho.Stetho
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyTasksApplication : Application() {

    @Inject
    lateinit var taskNotificationManager: TaskNotificationManager

    @Inject
    lateinit var settingSharedPreference: ISettingSharedPreference

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
        setUpTheme()
    }

    private fun setUpTheme() {
        when (settingSharedPreference.getTheme()) {
            ThemeType.THEME_DEFAULT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
            ThemeType.THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            ThemeType.THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }
}