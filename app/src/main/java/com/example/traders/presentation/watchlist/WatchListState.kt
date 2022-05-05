package com.example.traders.presentation.watchlist

import com.example.traders.database.SortOrder
import com.example.traders.network.models.binance24HourData.BinanceDataItem

data class WatchListState(
    val binanceCryptoData: List<BinanceDataItem> = emptyList(),
    val isRefreshing: Boolean = false,
    val showFavourites: Boolean = false,
    val sortOrder: SortOrder = SortOrder.DEFAULT,
    val shouldScrollTop: Boolean = false
)
