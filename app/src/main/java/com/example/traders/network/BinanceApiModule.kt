package com.example.traders.network

import com.example.traders.debug.FlipperInitializer.applyFlipperNetworkInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://api.binance.com"

@Module
@InstallIn(SingletonComponent::class)
class BinanceApiModule {
    @Provides
    fun provideBinanceApi(): BinanceApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(AuthenticationInterceptor())
                    .applyFlipperNetworkInterceptor()
                    .build()
            )
            .build()
            .create(BinanceApi::class.java)
    }
}
