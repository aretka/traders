package com.example.traders.watchlist.cryptoData.cryptoStatsData

data class Data(
    val all_time_high: AllTimeHigh,
    val market_data: MarketData,
    val marketcap: Marketcap,
    val roi_data: RoiData,
    val slug: String,
    val symbol: String
)