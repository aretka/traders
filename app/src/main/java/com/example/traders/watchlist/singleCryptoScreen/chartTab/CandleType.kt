package com.example.traders.watchlist.singleCryptoScreen.chartTab

enum class CandleType(val numDays: Long, val candleInterval: String) {
    DAILY(90, "1d"),
    WEEKLY(360, "1w")
}