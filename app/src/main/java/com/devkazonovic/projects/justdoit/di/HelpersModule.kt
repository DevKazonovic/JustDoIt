package com.devkazonovic.projects.justdoit.di

import android.app.AlarmManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HelpersModule {

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    @Provides
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Provides
    fun provideClock(): Clock = Clock.system(ZoneId.systemDefault())

}