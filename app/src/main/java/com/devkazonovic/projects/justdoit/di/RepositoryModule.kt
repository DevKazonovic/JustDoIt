package com.devkazonovic.projects.justdoit.di

import com.devkazonovic.projects.justdoit.data.repository.ITasksRepository
import com.devkazonovic.projects.justdoit.data.repository.TasksRepository
import com.devkazonovic.projects.justdoit.domain.mapper.IMappers
import com.devkazonovic.projects.justdoit.domain.mapper.Mappers
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun provideTasksRepository(repository: TasksRepository): ITasksRepository


    @Singleton
    @Binds
    abstract fun provideMappers(mappers: Mappers): IMappers
}