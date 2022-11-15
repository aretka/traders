package com.example.traders.network.models.cryptoPriceData

data class MarketData(
    val ohlcv_last_24_hour: OhlcvLast24Hour,
    val percent_change_usd_last_24_hours: Double
)
