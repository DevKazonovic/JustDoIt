package com.devkazonovic.projects.mytasks.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.devkazonovic.projects.mytasks.data.local.db.converter.MyTasksTypeConverters
import com.devkazonovic.projects.mytasks.data.local.db.dao.CategoryDao
import com.devkazonovic.projects.mytasks.data.local.db.dao.TaskDao
import com.devkazonovic.projects.mytasks.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskEntity

@Database(entities = [TaskEntity::class, CategoryEntity::class], version = 9)
@TypeConverters(MyTasksTypeConverters::class)
abstract class TasksDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao
}