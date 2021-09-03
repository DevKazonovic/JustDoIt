package com.devkazonovic.projects.justdoit.help.util

object RandomUtil {
    fun getRandomInt() = System.currentTimeMillis().toInt() % Int.MAX_VALUE
}