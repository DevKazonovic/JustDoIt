package com.devkazonovic.projects.mytasks.help.util

import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

fun stringToOffsetDateTime(value: String?): OffsetDateTime? {
    return value?.let {
        return if (value.isNotEmpty())
            formatter.parse(value, OffsetDateTime::from)
        else
            null
    }
}

fun offsetDateTimeToString(date: OffsetDateTime?): String? {
    return date?.format(formatter)
}
