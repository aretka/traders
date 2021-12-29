package com.example.traders.network

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://data.messari.io"

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {
    @Singleton
    @Provides
        fun provideCryptoApi(): CryptoApi{
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CryptoApi::class.java)
        }

}