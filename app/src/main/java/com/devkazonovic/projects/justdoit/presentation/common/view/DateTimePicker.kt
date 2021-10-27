package com.devkazonovic.projects.justdoit.presentation.common.view

import android.content.Context
import android.text.format.DateFormat.is24HourFormat
import com.devkazonovic.projects.justdoit.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

private val constraintsBuilder = CalendarConstraints.Builder()
    .setValidator(DateValidatorPointForward.now())

fun createDatePicker(
    context: Context,
    todayInUtcMilliseconds: Long,
    onPositiveClick: (date: Long) -> Unit,
): MaterialDatePicker<Long> {
    return MaterialDatePicker.Builder.datePicker()
        .setTitleText(context.getString(R.string.lable_selectdate))
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
    typeFormat: com.devkazonovic.projects.justdoit.domain.model.TimeFormat,
    onPositiveClick: (hour: Int, minute: Int) -> Unit,
    onCancelClick: () -> Unit,
): MaterialTimePicker {

    val clockFormat = when (typeFormat) {
        com.devkazonovic.projects.justdoit.domain.model.TimeFormat.CLOCK_DEFAULT -> {
            val isSystem24Hour = is24HourFormat(context)
            if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        }

        com.devkazonovic.projects.justdoit.domain.model.TimeFormat.CLOCK_12H -> {
            TimeFormat.CLOCK_12H
        }

        com.devkazonovic.projects.justdoit.domain.model.TimeFormat.CLOCK_24H -> {
            TimeFormat.CLOCK_24H
        }
    }

    return MaterialTimePicker.Builder()
        .setTitleText(context.getString(R.string.label_selecttime))
        .setHour(hour)
        .setMinute(minute)
        .setTimeFormat(clockFormat)
        .build().also { timePicker ->
            timePicker.addOnPositiveButtonClickListener {
                onPositiveClick(timePicker.hour, timePicker.minute)
            }


            timePicker.addOnNegativeButtonClickListener {
                onCancelClick()
            }
        }
}

