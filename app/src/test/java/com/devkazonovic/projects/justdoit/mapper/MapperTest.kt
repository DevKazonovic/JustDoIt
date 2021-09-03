package com.devkazonovic.projects.justdoit.mapper

import com.devkazonovic.projects.justdoit.data.local.db.entity.TaskNotificationEntity
import com.devkazonovic.projects.justdoit.domain.mapper.TaskNotificationEntityMapper
import com.devkazonovic.projects.justdoit.domain.mapper.TaskNotificationMapper
import com.devkazonovic.projects.justdoit.domain.model.TaskNotification
import com.devkazonovic.projects.justdoit.domain.model.TaskNotificationState
import com.google.common.truth.Truth
import org.junit.Test

class MapperTest {

    private val taskNotificationMapper = TaskNotificationMapper()
    private val taskNotificationEntityMapper = TaskNotificationEntityMapper()


    @Test
    fun mapTaskNotificationToTaskNotificationEntity() {
        //Given
        val taskNotification = TaskNotification(
            0,
            TaskNotificationState.NOT_NOTIFY
        )

        //When
        val taskNotificationEntity = taskNotificationMapper.map(
            taskNotification
        )

        //Then

        Truth.assertThat(taskNotificationEntity).isEqualTo(
            TaskNotificationEntity(
                0,
                "NOT_NOTIFY"
            )
        )
    }


    @Test
    fun mapTaskNotificationEntityToTaskNotification() {
        //Given
        val taskNotificationEntity = TaskNotificationEntity(
            0,
            "CANCELED"
        )

        //When
        val taskNotification = taskNotificationEntityMapper.map(
            taskNotificationEntity
        )

        //Then

        Truth.assertThat(taskNotification).isEqualTo(
            TaskNotification(
                0,
                TaskNotificationState.CANCELED
            )
        )
    }
}