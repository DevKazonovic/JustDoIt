package com.devkazonovic.projects.mytasks.di

import com.devkazonovic.projects.mytasks.data.local.preference.IMainSharedPreference
import com.devkazonovic.projects.mytasks.data.local.preference.ISettingSharedPreference
import com.devkazonovic.projects.mytasks.data.local.preference.MySharedPreferences
import com.devkazonovic.projects.mytasks.data.local.preference.SettingSharedPreference
import com.devkazonovic.projects.mytasks.data.local.source.ILocalDataSource
import com.devkazonovic.projects.mytasks.data.local.source.LocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataModule {

    @Singleton
    @Binds
    abstract fun provideLocalDataSource(localDataSource: LocalDataSource): ILocalDataSource


    @Singleton
    @Binds
    abstract fun provideMainSharedPreferences(sharedPreferences: MySharedPreferences): IMainSharedPreference

    @Singleton
    @Binds
    abstract fun provideSettingSharedPreferences(sharedPreferences: SettingSharedPreference): ISettingSharedPreference

}