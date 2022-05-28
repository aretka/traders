package com.example.traders.di

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.traders.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class NavigationModule {

    @Provides
    @ActivityScoped
    fun provideNavigationController(activity: Activity): NavController {
        return activity.findNavController(R.id.nav_host_fragment)
    }
}
