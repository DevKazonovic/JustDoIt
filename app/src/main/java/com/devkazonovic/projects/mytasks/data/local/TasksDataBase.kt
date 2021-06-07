package com.devkazonovic.projects.mytasks.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devkazonovic.projects.mytasks.data.local.dao.TasksDao
import com.devkazonovic.projects.mytasks.data.local.entities.MyTasksTypeConverters
import com.devkazonovic.projects.mytasks.data.local.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.local.entities.TaskListEntity

@Database(entities = [TaskEntity::class, TaskListEntity::class], version = 5)
@TypeConverters(MyTasksTypeConverters::class)
abstract class TasksDataBase : RoomDatabase() {
    abstract fun tasksDao(): TasksDao
}