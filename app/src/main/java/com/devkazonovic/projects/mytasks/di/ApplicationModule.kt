package com.devkazonovic.projects.mytasks.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devkazonovic.projects.mytasks.data.TasksRepositoryImpl
import com.devkazonovic.projects.mytasks.data.local.TasksDataBase
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import com.devkazonovic.projects.mytasks.domain.repository.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationModule {

    @Binds
    abstract fun provideTasksRepository(repository: TasksRepositoryImpl): TasksRepository

    companion object {
        @Singleton
        @Provides
        fun provideTaskDataBase(
            @ApplicationContext context: Context,
            sharedPreferences: MySharedPreferences
        ): TasksDataBase {
            return Room.databaseBuilder(
                context,
                TasksDataBase::class.java,
                "MyTasks.db"
            ).addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Timber.d("OnCreate DB")
                        db.execSQL("INSERT INTO task_list (id,name,isDefault) VALUES(0,'My List',1)")
                        sharedPreferences.saveCurrentTasksList(0)
                    }
                }
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}