package com.devkazonovic.projects.mytasks.service

import org.threeten.bp.*
import javax.inject.Inject

private const val HOUR_IN_MILLIS = 3_600_000L
private const val MINUTE_IN_MILLIS = 60_000L

class DateTimeHelper @Inject constructor(
    private val clock: Clock
) {

    private val currentUserZone = clock.zone

    fun showDate(dateInMillis: Long): String {
        val localDate = Instant.ofEpochMilli(dateInMillis).atZone(currentUserZone).toLocalDate()
        return showDate(localDate)
    }

    private fun showDate(
        dayOfWeekName: DayOfWeek,
        month: Month,
        dayOfMonth: Int,
        year: Int
    ): String {
        return "${dayOfWeekName.name}, ${month.name} ${dayOfMonth}, ${year}\n"
    }

    fun showTime(hour: Int, minute: Int): String {
        return "${if (hour in (0..9)) "0${hour}" else "$hour"}:${if (minute in (0..9)) "0${minute}" else "$minute"}"
    }

    fun showDateTime(timeStampInMillis: Long): String {
        val localDateTime = Instant.ofEpochMilli(timeStampInMillis)
            .atZone(currentUserZone)
            .toLocalDateTime()
        return showDate(localDateTime.toLocalDate()) + showTime(localDateTime.toLocalTime())
    }

    private fun showDate(localDate: LocalDate): String {
        return showDate(localDate.dayOfWeek, localDate.month, localDate.dayOfMonth, localDate.year)
    }

    private fun showTime(localTime: LocalTime): String {
        val hour = localTime.hour
        val minute = localTime.minute
        return showTime(hour, minute)
    }

    fun groupDateTime(date: Long, hour: Int, minute: Int): Long {
        val localDate = Instant.ofEpochMilli(date)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()

        val localTime = LocalTime.of(hour, minute)
        val localDateTime = LocalDateTime.of(localDate, localTime)

        return localDateTime.atZone(currentUserZone).toInstant().toEpochMilli()
    }

    fun isAfterNow(dateTimeInMillis: Long): Boolean {
        val currentInstant = Instant.now(clock)
        val instant = Instant.ofEpochMilli(dateTimeInMillis)
        return instant.isAfter(currentInstant)
    }

    fun isBeforeNow(dateTimeInMillis: Long): Boolean {
        val currentInstant = Instant.now(clock)
        val instant = Instant.ofEpochMilli(dateTimeInMillis)
        return instant.isBefore(currentInstant)
    }

    fun isNow(dateTimeInMillis: Long): Boolean {
        val currentInstant = Instant.ofEpochMilli(
            Instant.now(clock).epochSecond * 1000L
        )
        val instant = Instant.ofEpochMilli(dateTimeInMillis)
        return instant.equals(currentInstant)
    }

    fun getDateInMillis(timeInMillis: Long): Long {
        val zonedDateTime = Instant.ofEpochMilli(timeInMillis).atZone(currentUserZone)

        return zonedDateTime.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    fun getTimeInHourMinute(timeInMillis: Long): Pair<Int, Int> {
        val zonedDateTime = Instant.ofEpochMilli(timeInMillis).atZone(currentUserZone)
        return Pair(zonedDateTime.hour, zonedDateTime.minute)
    }
}