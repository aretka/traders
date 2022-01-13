package com.example.traders.watchlist.cryptoData.cryptoPriceData

data class CryptoPriceResponse(
    val `data`: List<Data>,
    val status: Status
)
