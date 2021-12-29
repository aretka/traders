package com.example.traders.watchlist.cryptoData.cryptoStatsData

data class OhlcvLast24Hour(
    val close: Double,
    val high: Double,
    val low: Double,
    val `open`: Double,
    val volume: Double
)