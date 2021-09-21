package com.devkazonovic.projects.justdoit.presentation.setting

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.devkazonovic.projects.justdoit.BuildConfig
import com.devkazonovic.projects.justdoit.R
import com.devkazonovic.projects.justdoit.domain.model.ThemeType
import com.devkazonovic.projects.justdoit.help.util.log
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


private const val KEY_SETTING_TIME_FORMAT = "KEY SETTING TIME FORMAT"
private const val KEY_SETTING_THEME = "KEY SETTING THEME"
private const val KEY_SETTING_RATE = "KEY SETTING RATE US"
private const val KEY_SETTING_SHARE = "KEY SETTING SHARE"
private const val KEY_SETTING_POLICY = "KEY SETTING POLICY"
private const val KEY_SETTING_VERSION = "KEY SETTING VERSION"

@AndroidEntryPoint
class MainSettingFragment :
    PreferenceFragmentCompat(),
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

        val ratePreference: Preference? = findPreference(KEY_SETTING_RATE)
        val sharePreference: Preference? = findPreference(KEY_SETTING_SHARE)
        val versionPreference: Preference? = findPreference(KEY_SETTING_VERSION)
        val policyPreference: Preference? = findPreference(KEY_SETTING_POLICY)

        sharePreference?.setOnPreferenceClickListener {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey, check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
            true
        }
        ratePreference?.setOnPreferenceClickListener {
            val manager = ReviewManagerFactory.create(requireContext())
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo = task.result
                    manager.launchReviewFlow(requireActivity(), reviewInfo)
                } else {
                    log("Error")
                }
            }
            true
        }
        versionPreference?.title = "Version: ${BuildConfig.VERSION_NAME}"
        policyPreference?.setOnPreferenceClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://justdoit-privacy-policy.vercel.app/")
            )
            startActivity(intent)
            true
        }

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