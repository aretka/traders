package com.example.traders.network.models.cryptoChartData

data class ValuesSchema(
    val close: String,
    val high: String,
    val low: String,
    val `open`: String,
    val timestamp: String,
    val volume: String
)