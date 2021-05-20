package com.devkazonovic.projects.mytasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devkazonovic.projects.mytasks.data.db.TasksDataBase
import com.devkazonovic.projects.mytasks.data.db.entities.TaskEntity
import com.devkazonovic.projects.mytasks.data.db.entities.TaskListEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyTasksDataBaseTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TasksDataBase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TasksDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        database.tasksDao().insert(DEFAULT_LIST)
    }

    @After
    fun closeDb() {
        database.close()
    }


    @Test
    fun insertAndGetAllTasks() {
        database.tasksDao().insert(TASK_UNCOMPLETED).blockingAwait()
        database.tasksDao().insert(TASK_COMPLETED).blockingAwait()

        database.tasksDao().getAllTasks()
            .test()
            .assertValue {
                it.contains(TASK_COMPLETED) && it.contains(TASK_UNCOMPLETED)
            }
    }

    @Test
    fun getCompletedTasks() {
        database.tasksDao().insert(TASK_UNCOMPLETED).blockingAwait()
        database.tasksDao().insert(TASK_COMPLETED).blockingAwait()
        database.tasksDao().getCompletedTasks()
            .test()
            .assertValue {
                it.contains(TASK_COMPLETED) && !it.contains(TASK_UNCOMPLETED)
            }
    }

    @Test
    fun markTaskAsCompleted() {
        database.tasksDao().insertAndReturnID(TASK_UNCOMPLETED).blockingSubscribe { id ->
            database.tasksDao().markTaskAsCompleted(id, 1).blockingAwait()
        }
        database.tasksDao().insert(TASK_COMPLETED).blockingAwait()
        database.tasksDao().getCompletedTasks()
            .test()
            .assertValue {
                it.contains(TASK_COMPLETED) && it.contains(TASK_UNCOMPLETED_COMPLETED)
            }

    }

    @Test
    fun updateTask() {
        database.tasksDao().insertAndReturnID(TASK_UNCOMPLETED)
            .blockingSubscribe { id ->
                database.tasksDao().getTask(id).blockingSubscribe {
                    TASK_UNCOMPLETED_UPDATE.id = it.id
                    database.tasksDao().update(TASK_UNCOMPLETED_UPDATE).blockingAwait()
                }
            }

        database.tasksDao().getAllTasks().test()
            .assertValue { it.contains(TASK_UNCOMPLETED_UPDATE) }
    }

    @Test
    fun deleteTask() {

        database.tasksDao().insertAndReturnID(TASK_UNCOMPLETED)
            .blockingSubscribe { id ->
                database.tasksDao().getTask(id).blockingSubscribe {
                    database.tasksDao().delete(it).blockingAwait()
                }
            }

        database.tasksDao().getAllTasks().test()
            .assertValue { it.isEmpty() }
    }

    @Test
    fun clearAllCompletedTasks() {
        database.tasksDao().insert(TASK_UNCOMPLETED).blockingAwait()
        database.tasksDao().insert(TASK_COMPLETED).blockingAwait()

        database.tasksDao().clearCompletedTasks()
        database.tasksDao().getAllTasks()
            .test()
            .assertValue {
                it.contains(TASK_UNCOMPLETED)
            }
    }


    companion object {
        private val DEFAULT_LIST = TaskListEntity("MyList").apply {
            this.id = 0
        }

        private val TASK_UNCOMPLETED = TaskEntity("Task1", "Detail", 0, DEFAULT_LIST.id)
        private val TASK_UNCOMPLETED_COMPLETED = TaskEntity("Task1", "Detail", 1, DEFAULT_LIST.id)
        private val TASK_UNCOMPLETED_UPDATE =
            TaskEntity("Task1 Update", "Detail Update", 0, DEFAULT_LIST.id)

        private val TASK_COMPLETED = TaskEntity("Task2", "Detail", 1, DEFAULT_LIST.id)

    }
}