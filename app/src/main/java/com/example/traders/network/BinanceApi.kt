package com.example.traders.network

import com.example.traders.watchlist.cryptoData.ServerTime
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24Data
import retrofit2.Response
import retrofit2.http.GET

interface BinanceApi {

    @GET("api/v3/time")
    suspend fun checkServerTime(): Response<ServerTime>

    @GET("/api/v3/ticker/24hr")
    suspend fun get24HourData(): Response<Binance24Data>
}