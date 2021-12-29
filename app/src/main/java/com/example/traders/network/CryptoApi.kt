package com.example.traders.network

import com.example.traders.watchlist.cryptoData.CryptoData
import retrofit2.Response
import retrofit2.http.GET

interface CryptoApi {
    @GET("/api/v2/assets?fields=id,slug,symbol,metrics/market_data/ohlcv_last_24_hour")
    suspend fun getCryptoPrices(): Response<CryptoData>
}
