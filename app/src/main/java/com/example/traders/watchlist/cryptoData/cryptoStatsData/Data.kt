package com.example.traders.watchlist.cryptoData.cryptoStatsData

data class Data(
    val all_time_high: AllTimeHigh,
    val id: String,
    val market_data: MarketData,
    val marketcap: Marketcap,
    val roi_by_year: RoiByYear,
    val roi_data: RoiData,
    val slug: String,
    val supply: Supply,
    val symbol: String
)