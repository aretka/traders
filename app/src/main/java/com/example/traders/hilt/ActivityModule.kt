package com.example.traders.hilt

import com.example.traders.SomeInjectedClass
import com.example.traders.SomeInjectedInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun bindSomeInjectedInterface(impl: SomeInjectedClass): SomeInjectedInterface
}
