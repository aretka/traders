package com.example.traders.watchlist.cryptoData.cryptoPriceData

data class Data(
    val id: String,
    val metrics: Metrics,
    val slug: String,
    val symbol: String,
    var imageURL: String
)