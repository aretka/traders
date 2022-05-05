package com.example.traders.network.models.cryptoPriceData

data class CryptoPriceResponse(
    val `data`: List<Data>,
    val status: Status
)
