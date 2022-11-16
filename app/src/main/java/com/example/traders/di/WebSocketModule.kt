package com.example.traders.di

import com.example.traders.network.webSocket.BinanceWSClient
import com.example.traders.network.webSocket.BinanceWSClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class WebSocketModule {

    // More than 1 instances of WebSocketClient are created without Singleton annotation
    @Binds
    @Singleton
    abstract fun provideBinanceWSClient(
        binanceWSClientImpl: BinanceWSClientImpl
    ): BinanceWSClient
}
