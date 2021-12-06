package com.example.traders.watchlist

data class CryptoInfo(
    var name: String,
    var fullName: String,
    var price: Int,
    var priceChange: Int,
    var percentagePriceChange: Double)
