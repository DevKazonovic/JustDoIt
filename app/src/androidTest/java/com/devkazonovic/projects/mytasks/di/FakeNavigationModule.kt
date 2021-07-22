package com.devkazonovic.projects.mytasks.di

import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.ui.AppBarConfiguration
import androidx.test.core.app.ApplicationProvider
import com.devkazonovic.projects.mytasks.R
import dagger.Module
import dagger.Provides
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [ActivityComponent::class],
    replaces = [NavigationModule::class]
)
object FakeNavigationComponentsModule {

    @Provides
    fun provideNavController(): NavController {
        return TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
            setGraph(R.navigation.main_graph)
        }
    }

    @Provides
    fun provideAppbarConfiguration(navController: NavController): AppBarConfiguration {
        return AppBarConfiguration(navController.graph)
    }
}