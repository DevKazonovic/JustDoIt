package com.devkazonovic.projects.mytasks.receiver

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Rule
import org.junit.rules.TestRule


class BootBroadcastReceiverTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()


}