package com.devkazonovic.projects.mytasks.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.devkazonovic.projects.mytasks.data.local.db.TasksDataBase
import com.devkazonovic.projects.mytasks.data.local.db.dao.CategoryDao
import com.devkazonovic.projects.mytasks.data.local.db.dao.TaskDao
import com.devkazonovic.projects.mytasks.data.local.db.dao.TaskNotificationDao
import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskNotificationEntity
import com.devkazonovic.projects.mytasks.domain.model.TaskNotification
import com.devkazonovic.projects.mytasks.domain.model.TaskNotificationState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TaskNotificationDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TasksDataBase
    private lateinit var taskNotificationDao: TaskNotificationDao


    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TasksDataBase::class.java
        )
            .allowMainThreadQueries()
            .build()

        taskNotificationDao= database.taskNotificationDao()
    }

    @After
    fun closeDb() {
        database.close()
    }


    @Test
    fun insertNotification(){
        taskNotificationDao.insert(NOTIFICATION).blockingAwait()
        taskNotificationDao.getAllTaskNotification().test()
            .assertValuesOnly(listOf(NOTIFICATION))
    }

    @Test
    fun updateNotification(){
        taskNotificationDao.insert(NOTIFICATION).blockingAwait()
        taskNotificationDao.updateState(NOTIFICATION.id,TaskNotificationState.SHOWING.name).blockingAwait()

        taskNotificationDao.getAllTaskNotification().test()
            .assertValuesOnly(listOf(NOTIFICATION_SHOWING))
    }


    @Test
    fun deleteNotification(){
        taskNotificationDao.insert(NOTIFICATION).blockingAwait()
        taskNotificationDao.deleteById(NOTIFICATION.id).blockingAwait()
        taskNotificationDao.getAllTaskNotification().test()
            .assertValuesOnly(emptyList())
    }

    @Test
    fun findNotification(){
        taskNotificationDao.insert(NOTIFICATION).blockingAwait()
        taskNotificationDao.getTaskNotificationById(0).test()
            .assertResult(NOTIFICATION)
    }



    companion object{
        val NOTIFICATION = TaskNotificationEntity(
            0,
            TaskNotificationState.NOT_NOTIFY.name
        )

        val NOTIFICATION_SHOWING = TaskNotificationEntity(
            0,
            TaskNotificationState.SHOWING.name
        )
    }

}