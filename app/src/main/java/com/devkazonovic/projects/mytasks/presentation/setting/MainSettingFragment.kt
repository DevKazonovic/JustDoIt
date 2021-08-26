package com.devkazonovic.projects.mytasks.presentation.setting

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.devkazonovic.projects.mytasks.R
import com.devkazonovic.projects.mytasks.domain.model.ThemeType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val KEY_SETTING_TIME_FORMAT = "KEY SETTING TIME FORMAT"
private const val KEY_SETTING_THEME = "KEY SETTING THEME"

@AndroidEntryPoint
class MainSettingFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var navController: NavController

    @Inject
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_setting, rootKey)

        val listPreference: ListPreference? = findPreference(KEY_SETTING_TIME_FORMAT)
        listPreference?.summaryProvider = Preference.SummaryProvider<ListPreference> { preference ->
            val value = preference.entry
            value
        }

        val themePreference: ListPreference? = findPreference(KEY_SETTING_THEME)
        themePreference?.summaryProvider =
            Preference.SummaryProvider<ListPreference> { preference ->
                val value = preference.entry
                value
            }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Toolbar>(R.id.toolbar)
            ?.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        sharedPreferences?.let {
            if (key == KEY_SETTING_THEME) {
                val theme =
                    it.getString(key, ThemeType.THEME_DEFAULT.name) ?: ThemeType.THEME_DEFAULT.name
                when (ThemeType.valueOf(theme)) {
                    ThemeType.THEME_DEFAULT -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    ThemeType.THEME_DARK -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }

                    ThemeType.THEME_LIGHT -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
            .unregisterOnSharedPreferenceChangeListener(this)
    }
}