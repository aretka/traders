package com.example.traders.network.models.binance24HourData

import com.google.gson.annotations.SerializedName

data class BinanceDataItem(
    val symbol: String = "",
    @SerializedName("lastPrice")
    val last: String = "",
    @SerializedName("highPrice")
    val high: String = "",
    @SerializedName("lowPrice")
    val low: String = "",
    @SerializedName("openPrice")
    val open: String = "",
    val priceChange: String = "",
    val priceChangePercent: String = "",
    val isFavourite: Boolean = false
)
