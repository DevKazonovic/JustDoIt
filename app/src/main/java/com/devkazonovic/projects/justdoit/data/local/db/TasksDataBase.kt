package com.devkazonovic.projects.justdoit.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devkazonovic.projects.justdoit.data.local.db.converter.MyTasksTypeConverters
import com.devkazonovic.projects.justdoit.data.local.db.dao.CategoryDao
import com.devkazonovic.projects.justdoit.data.local.db.dao.TaskDao
import com.devkazonovic.projects.justdoit.data.local.db.dao.TaskNotificationDao
import com.devkazonovic.projects.justdoit.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskEntity
import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskNotificationEntity

@Database(
    entities = [TaskEntity::class, CategoryEntity::class, TaskNotificationEntity::class],
    version = 15
)
@TypeConverters(MyTasksTypeConverters::class)
abstract class TasksDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun taskNotificationDao(): TaskNotificationDao
}