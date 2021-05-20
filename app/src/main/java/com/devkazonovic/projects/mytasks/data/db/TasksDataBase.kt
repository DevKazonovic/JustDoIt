package com.devkazonovic.projects.mytasks.data.db

import android.content.Context
import android.os.HandlerThread
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.devkazonovic.projects.mytasks.data.db.dao.TasksDao
import com.devkazonovic.projects.mytasks.data.db.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.db.entities.TaskListEntity
import com.devkazonovic.projects.mytasks.domain.MySharedPreferences
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asExecutor
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.ThreadPoolExecutor

@Database(entities = [TaskEntity::class, TaskListEntity::class], version = 3)
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
            ).addCallback(
                    object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT INTO task_list (id,name,isDefault) VALUES(0,'My List',1)")
                            MySharedPreferences(context).saveCurrentTasksList(0)
                        }
                    }
                )
                .fallbackToDestructiveMigration()
                .setQueryCallback(
                    { sqlQuery, bindArgs -> Timber.d("$sqlQuery, Args: $bindArgs") },
                    { Schedulers.io()}
                )
                .build()

    }
}