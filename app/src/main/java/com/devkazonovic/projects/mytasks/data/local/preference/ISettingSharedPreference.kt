package com.devkazonovic.projects.mytasks.data.local.preference

import com.devkazonovic.projects.mytasks.domain.model.ThemeType
import com.devkazonovic.projects.mytasks.domain.model.TimeFormat

interface ISettingSharedPreference {
    fun getTimeFormat(): TimeFormat
    fun getTheme(): ThemeType
}