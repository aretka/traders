package com.example.traders.hilt

import com.example.traders.webSocket.BinanceWSClient
import com.example.traders.webSocket.BinanceWSClientImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.net.URI
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class WebSocketModule {

    // More than 1 instances of WebSocketClient are created without Singleton annotation
    @Provides
    @Singleton
    fun provideBinanceWSClient(): BinanceWSClient {
        return BinanceWSClientImpl(URI(WEB_SOCKET_URI))
    }

    companion object {
        const val WEB_SOCKET_URI = "wss://stream.binance.com:9443/stream"
    }

}