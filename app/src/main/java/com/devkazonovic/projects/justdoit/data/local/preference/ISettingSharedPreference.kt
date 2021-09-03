package com.devkazonovic.projects.justdoit.data.local.preference

import com.devkazonovic.projects.justdoit.domain.model.ThemeType
import com.devkazonovic.projects.justdoit.domain.model.TimeFormat

interface ISettingSharedPreference {
    fun getTimeFormat(): TimeFormat
    fun getTheme(): ThemeType
}