package com.example.traders.network

import com.example.traders.watchlist.cryptoData.cryptoChartData.CryptoChartData
import com.example.traders.watchlist.cryptoData.cryptoPriceData.CryptoPriceData
import com.example.traders.watchlist.cryptoData.cryptoStatsData.CryptoStatistics
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CryptoApi {
    // Get request for recent crypto prices
    @GET("/api/v2/assets?limit=50&fields=id,slug,symbol," +
            "metrics/market_data/ohlcv_last_24_hour," +
            "metrics/market_data/percent_change_usd_last_24_hours")
    suspend fun getCryptoPrices(): Response<CryptoPriceData>

    // Get request for crypto price statistics
    @GET(
        "/api/v1/assets/{slug}/metrics?fields=id,slug,symbol," +
                "market_data/price_usd," +
                "market_data/percent_change_usd_last_1_hour," +
                "market_data/percent_change_usd_last_24_hours," +
                "marketcap/rank," +
                "marketcap/marketcap_dominance_percent," +
                "marketcap/current_marketcap_usd," +
                "supply/circulating," +
                "market_data/ohlcv_last_1_hour," +
                "market_data/ohlcv_last_24_hour," +
                "all_time_high," +
                "roi_data/percent_change_last_1_week," +
                "roi_data/percent_change_last_1_month," +
                "roi_data/percent_change_last_3_months," +
                "roi_data/percent_change_last_1_year," +
                "roi_by_year"
    )
    suspend fun getCryptoPriceStatistics(
        @Path("slug") slug: String
    ): Response<CryptoStatistics>

    // Get request for chart data
    @GET(
"/api/v1/assets/{slug}/metrics/price/time-series"
    )
    suspend fun getCryptoChartData(
        @Path("slug") slug: String,
        @Query("start") startDate: String,
        @Query("end") endDate: String,
        @Query("interval") interval: String,
    ): Response<CryptoChartData>
}