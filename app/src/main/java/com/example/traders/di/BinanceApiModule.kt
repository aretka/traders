package com.example.traders.di

import com.example.traders.debug.FlipperInitializer.addFlipperNetworkInterceptor
import com.example.traders.network.BinanceApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

private const val BASE_URL = "https://api.binance.com"

@Module
@InstallIn(SingletonComponent::class)
class BinanceApiModule {

    @Provides
    @Singleton
    fun provideBinanceApi(): BinanceApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addFlipperNetworkInterceptor()
                    .build()
            )
            .build()
            .create(BinanceApi::class.java)
    }
}
