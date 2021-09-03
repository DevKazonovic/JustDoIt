package com.devkazonovic.projects.justdoit

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.squareup.rx3.idler.Rx3Idler
import dagger.hilt.android.testing.HiltTestApplication
import io.reactivex.rxjava3.plugins.RxJavaPlugins

class HiltTestRunner : AndroidJUnitRunner() {

    override fun onStart() {
        RxJavaPlugins.setInitIoSchedulerHandler(Rx3Idler.create("Rxjava 3.0 Io Scheduler"))
        RxJavaPlugins.setInitComputationSchedulerHandler(Rx3Idler.create("Rxjava 3.0 computation Scheduler"))
        RxJavaPlugins.setInitNewThreadSchedulerHandler(Rx3Idler.create("Rxjava 3.0 new thread Scheduler"))
        RxJavaPlugins.setInitSingleSchedulerHandler(Rx3Idler.create("Rxjava 3.0 single scheduler Scheduler"))
        super.onStart()
    }

    override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}