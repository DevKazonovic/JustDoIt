package com.devkazonovic.projects.mytasks.help.util

import android.content.Context
import android.util.TypedValue
import androidx.core.content.ContextCompat

fun getThemeColor(context: Context, colorID: Int): Int {
    val typedValue = TypedValue()
    val currentTheme = context.theme
    currentTheme.resolveAttribute(colorID, typedValue, true)
    return typedValue.data
}

fun getColor(context: Context, colorID: Int): Int {
    return ContextCompat.getColor(context, colorID)
}