package com.example.traders.network

import com.example.traders.network.models.CryptoTicker
import com.example.traders.network.models.binance24HourData.Binance24DataItem
import com.example.traders.network.models.binanceCandleData.BinanceCandleDataList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BinanceApi {

    @GET("/api/v3/ticker/24hr")
    suspend fun get24HourData(): Response<List<Binance24DataItem>>

    @GET("/api/v3/ticker/price")
    suspend fun getBinanceTickerBySymbol(
        @Query("symbol") symbol: String
    ): Response<CryptoTicker>

    @GET("/api/v3/klines")
    suspend fun getBinanceCandleData(
        @Query("symbol") symbol: String,
        @Query("interval") interval: String,
        @Query("limit") limit: Int
    ): Response<BinanceCandleDataList>
}
