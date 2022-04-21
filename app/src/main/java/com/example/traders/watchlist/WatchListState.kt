package com.example.traders.watchlist

import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24HourData.BinanceDataItem

data class WatchListState(
    val binanceCryptoData: List<BinanceDataItem> = emptyList(),
    val isRefreshing: Boolean = false
)