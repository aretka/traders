package com.example.traders.network

import com.example.traders.watchlist.cryptoData.cryptoChartData.CryptoChartData
import com.example.traders.watchlist.cryptoData.cryptoDescData.CryptoDescData
import com.example.traders.watchlist.cryptoData.cryptoPriceData.CryptoPriceResponse
import com.example.traders.watchlist.cryptoData.cryptoStatsData.CryptoStatistics
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MessariApi {
    // Get request for recent crypto prices
    @GET("/api/v2/assets?limit=50&fields=id,slug,symbol," +
            "metrics/market_data/ohlcv_last_24_hour," +
            "metrics/market_data/percent_change_usd_last_24_hours")
    suspend fun getCryptoPrices(): Response<CryptoPriceResponse>

    // Get request for crypto price statistics
    @GET("/api/v1/assets/{slug}/metrics")
    suspend fun getCryptoPriceStatistics(
        @Path("slug") slug: String
    ): Response<CryptoStatistics>

    // Get request for chart data
    @GET(
"/api/v1/assets/{slug}/metrics/price/time-series"
    )
    suspend fun getCryptoChartData(
        @Path("slug") slug: String,
        @Query("after") afterDate: String,
        @Query("interval") interval: String,
    ): Response<CryptoChartData>

    @GET(
        "/api/v2/assets/{id}/profile?fields=" +
                "profile/general/overview/project_details," +
                "profile/general/background"
    )
    suspend fun getCryptoDescriptionData(
        @Path("id") id: String
    ): Response<CryptoDescData>
}
