package com.devkazonovic.projects.mytasks.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devkazonovic.projects.mytasks.data.local.db.TasksDataBase
import com.devkazonovic.projects.mytasks.data.local.db.dao.CategoryDao
import com.devkazonovic.projects.mytasks.data.local.db.dao.TaskDao
import com.devkazonovic.projects.mytasks.data.local.db.entity.CategoryEntity
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskEntity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TasksDataBase
    private lateinit var taskDao: TaskDao
    private lateinit var categoryDao: CategoryDao


    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TasksDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        taskDao = database.taskDao()
        categoryDao = database.categoryDao()


        categoryDao.insert(DEFAULT_LIST)
    }

    @After
    fun closeDb() {
        database.close()
    }


    @Test
    fun insertAndGetAllTasks() {
        database.taskDao().insert(TASK_UNCOMPLETED).blockingAwait()
        database.taskDao().insert(TASK_COMPLETED).blockingAwait()

        database.taskDao().getAllTasks()
            .test()
            .assertResult(listOf(TASK_UNCOMPLETED,TASK_COMPLETED))
    }

    @Test
    fun getCompletedTasks() {
        taskDao.insert(TASK_UNCOMPLETED).blockingAwait()
        taskDao.insert(TASK_COMPLETED).blockingAwait()

        taskDao.getCompletedTasks(0)
            .test()
            .assertValue {
                it.contains(TASK_COMPLETED) && !it.contains(TASK_UNCOMPLETED)
            }
    }

    @Test
    fun markTaskAsCompleted() {
        taskDao.insertAndReturnId(TASK_UNCOMPLETED).blockingSubscribe { id ->
            taskDao.markTaskAsCompleted(id, "").blockingAwait()
        }
        taskDao.insert(TASK_COMPLETED).blockingAwait()
        taskDao.getCompletedTasks(0)
            .test()
            .assertValue {
                it.contains(TASK_COMPLETED) && it.contains(TASK_UNCOMPLETED_COMPLETED)
            }

    }

    @Test
    fun updateTask() {
        taskDao.insertAndReturnId(TASK_UNCOMPLETED)
            .blockingSubscribe { id ->
                taskDao.getTask(id).blockingSubscribe {
                    TASK_UNCOMPLETED_UPDATE.id = it.id
                    taskDao.update(TASK_UNCOMPLETED_UPDATE).blockingAwait()
                }
            }

        taskDao.getAllTasks().test()
            .assertValue { it.contains(TASK_UNCOMPLETED_UPDATE) }
    }

    @Test
    fun deleteTask() {

        taskDao.insertAndReturnId(TASK_UNCOMPLETED)
            .blockingSubscribe { id ->
                taskDao.getTask(id).blockingSubscribe {
                    database.taskDao().delete(it).blockingAwait()
                }
            }

        taskDao.getAllTasks().test()
            .assertValue { it.isEmpty() }
    }

    @Test
    fun clearAllCompletedTasks() {
        taskDao.insert(TASK_UNCOMPLETED).blockingAwait()
        taskDao.insert(TASK_COMPLETED).blockingAwait()

        taskDao.clearCompletedTasks()
        taskDao.getAllTasks()
            .test()
            .assertValue {
                it.contains(TASK_UNCOMPLETED)
            }
    }


    companion object {
        private val DEFAULT_LIST = CategoryEntity("MyList").apply {
            this.id = 0
        }
        private val TASK_UNCOMPLETED = TaskEntity(
            categoryId = DEFAULT_LIST.id,
            title = "Task1",
            detail = "Detail",
            isCompleted = 0
        )
        private val TASK_UNCOMPLETED_COMPLETED = TaskEntity(
            categoryId = DEFAULT_LIST.id,
            title = "Task1",
            detail = "Detail",
            isCompleted = 1
        )
        private val TASK_UNCOMPLETED_UPDATE =
            TaskEntity(
                categoryId = DEFAULT_LIST.id,
                title = "Task1 Update",
                detail = "Detail",
                isCompleted = 0
            )

        private val TASK_COMPLETED = TaskEntity(
            categoryId = DEFAULT_LIST.id,
            title = "Task2",
            detail = "Detail",
            isCompleted = 1
        )

    }

}