package com.example.traders.presentation.cryptoDetailsScreen.chartTab

enum class CandleType(val limit: Int, val candleInterval: String) {
    DAILY(90, "1d"),
    WEEKLY(52, "1w")
}
