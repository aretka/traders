package com.example.traders.network.models.cryptoChartData

data class Data(
    val id: String = "",
    val values: List<List<Float>> = listOf(emptyList())
)