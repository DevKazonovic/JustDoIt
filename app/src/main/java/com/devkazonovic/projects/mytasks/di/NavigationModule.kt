package com.devkazonovic.projects.mytasks.di

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@Module
@InstallIn(FragmentComponent::class)
object NavigationModule {

    @Provides
    fun provideNavController(fragment: Fragment): NavController {
        return fragment.findNavController()
    }

    @Provides
    fun provideAppbarConfiguration(navController: NavController): AppBarConfiguration {
        return AppBarConfiguration(navController.graph)
    }
}