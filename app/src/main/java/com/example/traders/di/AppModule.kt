package com.example.traders.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @ApplicationScopeDefault
    @Provides
    @Singleton
    fun provideApplicationScopeDefault() = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @ApplicationScopeIO
    @Provides
    @Singleton
    fun provideApplicationScopeIO() = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScopeDefault

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScopeIO