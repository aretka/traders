package com.example.traders.watchlist.cryptoData.cryptoChartData

data class Parameters(
    val asset_id: String,
    val asset_key: String,
    val columns: List<String>,
    val end: String,
    val format: String,
    val interval: String,
    val order: String,
    val start: String,
    val timestamp_format: String
)