package com.devkazonovic.projects.mytasks.domain

import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_IO
import com.devkazonovic.projects.mytasks.help.util.SCHEDULER_MAIN
import io.reactivex.rxjava3.core.Scheduler
import javax.inject.Inject
import javax.inject.Named

class RxScheduler @Inject constructor(
    @Named(SCHEDULER_MAIN) private val mainScheduler: Scheduler,
    @Named(SCHEDULER_IO) private val ioScheduler: Scheduler
) : IRxScheduler {
    override fun mainScheduler() = mainScheduler

    override fun ioScheduler() = ioScheduler
}