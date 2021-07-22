package com.devkazonovic.projects.mytasks.help.util

object RandomUtil {
    fun getRandomInt() = System.currentTimeMillis().toInt() % Int.MAX_VALUE
}