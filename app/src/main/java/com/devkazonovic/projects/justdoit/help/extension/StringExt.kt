package com.devkazonovic.projects.justdoit.help.extension

fun String.upperFirstChar(): String {
    val strBuilder = StringBuilder()
    strBuilder.append(this[0].uppercaseChar())
    strBuilder.append(this.substring(1, this.length).lowercase())
    return strBuilder.toString()
}