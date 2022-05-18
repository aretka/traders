package com.example.traders.network.models.cryptoChartData

data class CryptoChart(
    val volume: Float = 0f,
    val open: Float = 0f,
    val high: Float = 0f,
    val low: Float = 0f,
    val close: Float = 0f,
    val priceChange: Float,
    val percentPriceChange: Float,
)