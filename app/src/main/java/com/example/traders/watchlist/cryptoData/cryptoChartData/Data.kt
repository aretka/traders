package com.example.traders.watchlist.cryptoData.cryptoChartData

data class Data(
    val _internal_temp_agora_id: String,
    val contract_addresses: Any,
    val id: String,
    val name: String,
    val parameters: Parameters,
    val schema: Schema,
    val slug: String,
    val symbol: String,
    val values: List<List<Float>>
)