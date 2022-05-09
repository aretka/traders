package com.example.traders.di

import com.example.traders.debug.FlipperInitializer.addFlipperNetworkInterceptor
import com.example.traders.network.MessariApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://data.messari.io"

@Module
@InstallIn(SingletonComponent::class)
class MessariApiModule {
    @Singleton
    @Provides
        fun provideCryptoApi(): MessariApi {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().addFlipperNetworkInterceptor().build())
                .build()
                .create(MessariApi::class.java)
        }

}
