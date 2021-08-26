package com.devkazonovic.projects.mytasks.presentation.common.view

import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

private val constraintsBuilder = CalendarConstraints.Builder()
    .setValidator(DateValidatorPointForward.now())

fun createDatePicker(
    todayInUtcMilliseconds: Long,
    onPositiveClick: (date: Long) -> Unit,
): MaterialDatePicker<Long> {
    return MaterialDatePicker.Builder.datePicker()
        .setTitleText("Select date")
        .setSelection(todayInUtcMilliseconds)
        .build().also {
            it.addOnPositiveButtonClickListener { date ->
                onPositiveClick(date)
            }
        }
}


fun createTimePicker(
    hour: Int,
    minute: Int,
    context: Context,
    typeFormat: com.devkazonovic.projects.mytasks.domain.model.TimeFormat,
    onPositiveClick: (hour: Int, minute: Int) -> Unit,
): MaterialTimePicker {

    val clockFormat = when (typeFormat) {
        com.devkazonovic.projects.mytasks.domain.model.TimeFormat.CLOCK_DEFAULT -> {
            val isSystem24Hour = is24HourFormat(context)
            if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        }

        com.devkazonovic.projects.mytasks.domain.model.TimeFormat.CLOCK_12H -> {
            TimeFormat.CLOCK_12H
        }

        com.devkazonovic.projects.mytasks.domain.model.TimeFormat.CLOCK_24H -> {
            TimeFormat.CLOCK_24H
        }
    }

    return MaterialTimePicker.Builder()
        .setTitleText("Select date")
        .setHour(hour)
        .setMinute(minute)
        .setTimeFormat(clockFormat)
        .build().also { timePicker ->
            timePicker.addOnPositiveButtonClickListener {
                onPositiveClick(timePicker.hour, timePicker.minute)
            }
        }
}

