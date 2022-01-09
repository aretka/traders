package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import com.example.traders.watchlist.cryptoData.cryptoChartData.CryptoChartData

data class ChartData(
    val chartDataFor90d: List<List<Float>>? = null,
    val chartDataFor360d: List<List<Float>>? = null,
)
