package com.example.traders.watchlist.cryptoData

data class FixedCryptoList(
    val cryptoList: List<String> = listOf("BTC", "ETH", "XRP"),
    val cryptoIconList: Map<String, String> = mapOf(
        "BTC" to "https://cryptologos.cc/logos/bitcoin-btc-logo.png?v=018",
        "ETH" to "http//iconETHLink.com",
    )
)

