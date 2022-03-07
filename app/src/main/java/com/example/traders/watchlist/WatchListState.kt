package com.example.traders.watchlist

import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem

data class WatchListState(
    val binanceCryptoData: List<Binance24DataItem> = emptyList(),
    val isRefreshing: Boolean = false
)