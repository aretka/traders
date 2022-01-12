package com.example.traders.watchlist.cryptoData.cryptoChartData

data class Schema(
    val description: String,
    val metric_id: String,
    val minimum_interval: String,
    val name: String,
    val source_attribution: List<SourceAttribution>,
    val values_schema: ValuesSchema
)