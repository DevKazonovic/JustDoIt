package com.devkazonovic.projects.mytasks.di

import com.devkazonovic.projects.mytasks.domain.IRxScheduler
import com.devkazonovic.projects.mytasks.domain.RxScheduler
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_MAIN
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SchedulersModule {
    companion object {
        @Singleton
        @Named(SCHEDULER_MAIN)
        @Provides
        fun provideMainScheduler(): Scheduler = AndroidSchedulers.mainThread()

        @Singleton
        @Named(SCHEDULER_IO)
        @Provides
        fun provideIOScheduler(): Scheduler = Schedulers.io()
    }

    @Singleton
    @Binds
    abstract fun provideRxSchedulers(rxScheduler: RxScheduler): IRxScheduler
}