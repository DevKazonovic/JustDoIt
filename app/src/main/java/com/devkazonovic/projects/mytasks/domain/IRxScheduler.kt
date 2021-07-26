package com.devkazonovic.projects.mytasks.domain

import io.reactivex.rxjava3.core.Scheduler

interface IRxScheduler {
    fun mainScheduler(): Scheduler
    fun ioScheduler(): Scheduler
}