package com.devkazonovic.projects.justdoit.domain.mapper

import javax.inject.Inject

interface IMappers {
    fun taskEntityMapper(): TaskEntityMapper
    fun taskMapper(): TaskMapper
    fun categoryEntityMapper(): CategoryEntityMapper
    fun categoryMapper(): CategoryMapper
    fun taskNotificationEntityMapper(): TaskNotificationEntityMapper
    fun taskNotificationMapper(): TaskNotificationMapper
}

class Mappers @Inject constructor(
    private val taskEntityMapper: TaskEntityMapper,
    private val taskMapper: TaskMapper,
    private val categoryEntityMapper: CategoryEntityMapper,
    private val categoryMapper: CategoryMapper,
    private val taskNotificationEntityMapper: TaskNotificationEntityMapper,
    private val taskNotificationMapper: TaskNotificationMapper,
) : IMappers {
    override fun taskEntityMapper(): TaskEntityMapper = taskEntityMapper
    override fun taskMapper(): TaskMapper = taskMapper
    override fun categoryEntityMapper(): CategoryEntityMapper = categoryEntityMapper
    override fun categoryMapper(): CategoryMapper = categoryMapper
    override fun taskNotificationEntityMapper(): TaskNotificationEntityMapper =
        taskNotificationEntityMapper

    override fun taskNotificationMapper(): TaskNotificationMapper =
        taskNotificationMapper

}