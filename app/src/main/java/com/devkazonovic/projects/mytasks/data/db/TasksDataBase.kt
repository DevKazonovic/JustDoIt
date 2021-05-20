package com.devkazonovic.projects.mytasks.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devkazonovic.projects.mytasks.data.db.dao.TasksDao
import com.devkazonovic.projects.mytasks.data.db.entities.MyTasksTypeConverters
import com.devkazonovic.projects.mytasks.data.db.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.db.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import timber.log.Timber

@Database(entities = [TaskEntity::class, TaskListEntity::class], version = 5)
@TypeConverters(MyTasksTypeConverters::class)
abstract class TasksDataBase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao


    companion object {
        @Volatile
        private var INSTANCE: TasksDataBase? = null

        fun getInstance(context: Context): TasksDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                TasksDataBase::class.java,
                "MyTasks.db"
            )
                .addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Timber.d("OnCreate DB")
                            db.execSQL("INSERT INTO task_list (id,name,isDefault) VALUES(0,'My List',1)")
                            MySharedPreferences(context).saveCurrentTasksList(0)
                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .build()

    }
}