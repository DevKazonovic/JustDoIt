package com.devkazonovic.projects.justdoit.presentation.tasks.util

import android.content.Context
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.help.util.getThemeColor
import com.google.android.material.card.MaterialCardView

fun MaterialCardView.selectTaskCard(context: Context) {
    this.cardElevation = 8f
    this.strokeColor = getThemeColor(context, R.attr.colorPrimary)
    this.strokeWidth = 8
}

fun MaterialCardView.unSelectTaskCard(context: Context) {
    this.strokeWidth = 0
    this.cardElevation = 0f
}