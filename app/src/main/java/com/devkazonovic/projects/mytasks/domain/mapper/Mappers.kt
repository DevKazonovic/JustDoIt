package com.devkazonovic.projects.mytasks.domain.mapper

import javax.inject.Inject

interface IMappers {
    fun taskEntityMapper(): TaskEntityMapper
    fun taskMapper(): TaskMapper
    fun categoryEntityMapper(): CategoryEntityMapper
    fun categoryMapper(): CategoryMapper

}

class Mappers @Inject constructor(
    private val taskEntityMapper: TaskEntityMapper,
    private val taskMapper: TaskMapper,
    private val categoryEntityMapper: CategoryEntityMapper,
    private val categoryMapper: CategoryMapper
) : IMappers {
    override fun taskEntityMapper(): TaskEntityMapper = taskEntityMapper
    override fun taskMapper(): TaskMapper = taskMapper
    override fun categoryEntityMapper(): CategoryEntityMapper = categoryEntityMapper
    override fun categoryMapper(): CategoryMapper = categoryMapper
}