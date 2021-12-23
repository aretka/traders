package com.example.traders.watchlist.cryptoData.cryptoStatsData

data class MarketData(
    val ohlcv_last_1_hour: OhlcvLast1Hour,
    val ohlcv_last_24_hour: OhlcvLast24Hour,
    val percent_change_usd_last_1_hour: Double,
    val percent_change_usd_last_24_hours: Double,
    val price_usd: Double
)