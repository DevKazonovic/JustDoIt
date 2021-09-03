package com.devkazonovic.projects.justdoit.data.local.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.devkazonovic.projects.justdoit.domain.model.ThemeType
import com.devkazonovic.projects.justdoit.domain.model.TimeFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val KEY_SETTING_TIME_FORMAT = "KEY SETTING TIME FORMAT"
private const val KEY_SETTING_THEME = "KEY SETTING THEME"

class SettingSharedPreference @Inject constructor(
    @ApplicationContext context: Context,
) : ISettingSharedPreference {
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)
    override fun getTimeFormat(): TimeFormat {
        return preferences.getString(KEY_SETTING_TIME_FORMAT, TimeFormat.CLOCK_DEFAULT.name)?.let {
            TimeFormat.valueOf(it)
        } ?: TimeFormat.CLOCK_DEFAULT
    }

    override fun getTheme(): ThemeType {
        return preferences.getString(KEY_SETTING_THEME, ThemeType.THEME_DEFAULT.name)?.let {
            ThemeType.valueOf(it)
        } ?: ThemeType.THEME_DEFAULT
    }

}