package com.example.traders.network.models.binanceCandleData

data class BinanceChartCandle(
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val date: Long,
    val volume: Float
)