package com.devkazonovic.projects.justdoit.domain

import io.reactivex.rxjava3.core.Scheduler

interface IRxScheduler {
    fun mainScheduler(): Scheduler
    fun ioScheduler(): Scheduler
}