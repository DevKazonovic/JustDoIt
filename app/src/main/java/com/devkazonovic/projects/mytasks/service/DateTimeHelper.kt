package com.devkazonovic.projects.mytasks.service

import android.content.Context
import android.text.format.DateFormat
import com.devkazonovic.projects.mytasks.data.local.preference.ISettingSharedPreference
import com.devkazonovic.projects.mytasks.domain.model.TimeFormat
import com.devkazonovic.projects.mytasks.help.extension.upperFirstChar
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.*
import javax.inject.Inject

private const val HOUR_IN_MILLIS = 3_600_000L
private const val MINUTE_IN_MILLIS = 60_000L

class DateTimeHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val clock: Clock,
    private val mainSettingSharedPreference: ISettingSharedPreference,
) {

    private val currentUserZone = clock.zone


    fun fromLongToLocalDate(dateInMillis: Long): LocalDate {
        return Instant.ofEpochMilli(dateInMillis).atZone(ZoneOffset.UTC).toLocalDate()
    }

    fun showDate(dateInMillis: Long): String {
        val localDate = Instant.ofEpochMilli(dateInMillis).atZone(currentUserZone).toLocalDate()
        return showDate(localDate)
    }

    private fun showDate(
        dayOfWeekName: DayOfWeek,
        month: Month,
        dayOfMonth: Int,
        year: Int,
    ): String {
        return "${dayOfWeekName.name.upperFirstChar()}, ${month.name.upperFirstChar()} ${dayOfMonth}, $year"
    }

    fun showTime(hour: Int, minute: Int): String {

        return when (mainSettingSharedPreference.getTimeFormat()) {
            TimeFormat.CLOCK_DEFAULT -> {
                val isSystem24Hour = DateFormat.is24HourFormat(context)
                if (!isSystem24Hour) {
                    from24to12(hour, minute)
                } else {
                    from24(hour, minute)
                }
            }
            TimeFormat.CLOCK_24H -> {
                from24(hour, minute)
            }
            TimeFormat.CLOCK_12H -> {
                from24to12(hour, minute)
            }
        }
    }

    private fun from24to12(oldHour: Int, minute: Int): String {
        val newHour = if (oldHour == 0) 12 else if (oldHour in (1..12)) oldHour else (oldHour - 12)
        val time =
            "${if (newHour in (0..9)) "0${newHour}" else "$newHour"}:${if (minute in (0..9)) "0${minute}" else "$minute"}"
        return if (oldHour in (0..11)) "$time AM"
        else "$time PM"
    }

    private fun from24(hour: Int, minute: Int): String {
        return "${if (hour in (0..9)) "0${hour}" else "$hour"}:${if (minute in (0..9)) "0${minute}" else "$minute"}"
    }

    fun showTime(time: Pair<Int, Int>): String {
        val hour = time.first
        val minute = time.second
        return showTime(hour, minute)
    }

    fun showDateTime(timeStampInMillis: Long): String {
        val localDateTime = Instant.ofEpochMilli(timeStampInMillis)
            .atZone(currentUserZone)
            .toLocalDateTime()
        return showDate(localDateTime.toLocalDate()) + " " + showTime(localDateTime.toLocalTime())
    }

    private fun showDate(localDate: LocalDate): String {
        val localDateNow = LocalDate.now(clock)
        return when {
            localDate.isEqual(localDateNow) -> return "Today"
            localDate.isEqual(localDateNow.plusDays(1)) -> return "Tomorrow"
            else -> showDate(localDate.dayOfWeek,
                localDate.month,
                localDate.dayOfMonth,
                localDate.year)
        }
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

    fun groupDateTime(date: LocalDate, hour: Int, minute: Int): Long {

        val localTime = LocalTime.of(hour, minute)
        val localDateTime = LocalDateTime.of(date, localTime)

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

    fun isDateBeforeNow(dateInMillis: Long): Boolean {
        val currentDate = LocalDate.now()
        val localDate = Instant.ofEpochMilli(dateInMillis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
        return localDate.isBefore(currentDate)
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