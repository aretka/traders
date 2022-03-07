package com.example.traders.watchlist.cryptoData.cryptoChartData

data class Data(
    val id: String = "",
    val values: List<List<Float>> = listOf(emptyList())
)