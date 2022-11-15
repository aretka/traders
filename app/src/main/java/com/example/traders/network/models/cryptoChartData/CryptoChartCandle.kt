package com.example.traders.network.models.cryptoChartData

data class CryptoChartCandle(
    val volume: Float = 0f,
    val open: Float = 0f,
    val high: Float = 0f,
    val low: Float = 0f,
    val close: Float = 0f,
    val priceChange: Float,
    val percentPriceChange: Float,
    val date: String
)
