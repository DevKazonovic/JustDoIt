package com.devkazonovic.projects.mytasks.mapper

import com.devkazonovic.projects.mytasks.data.local.db.entity.TaskNotificationEntity
import com.devkazonovic.projects.mytasks.domain.mapper.Mappers
import com.devkazonovic.projects.mytasks.domain.mapper.TaskNotificationEntityMapper
import com.devkazonovic.projects.mytasks.domain.mapper.TaskNotificationMapper
import com.devkazonovic.projects.mytasks.domain.model.TaskNotification
import com.devkazonovic.projects.mytasks.domain.model.TaskNotificationState
import com.google.common.truth.Truth
import org.junit.Test

class MapperTest {

    private val taskNotificationMapper = TaskNotificationMapper()
    private val taskNotificationEntityMapper = TaskNotificationEntityMapper()


    @Test
    fun mapTaskNotificationToTaskNotificationEntity(){
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
    fun mapTaskNotificationEntityToTaskNotification(){
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