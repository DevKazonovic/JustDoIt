package com.devkazonovic.projects.justdoit.help.extension

import java.util.*

fun String.upperFirstChar(): String {
    val strBuilder = StringBuilder()
    strBuilder.append(this[0].toUpperCase())
    strBuilder.append(this.substring(1, this.length).toLowerCase(Locale.ROOT))
    return strBuilder.toString()
}