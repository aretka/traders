package com.example.traders.hilt

import android.app.Activity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.traders.MainActivity
import com.example.traders.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
class NavigationModule {

    @Provides
    fun provideNavigationController(activity: Activity): NavController {
        return activity.findNavController(R.id.nav_host_fragment)
    }
}
