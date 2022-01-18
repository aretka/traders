package com.example.traders.watchlist.cryptoData.binance24hTickerData

import com.google.gson.annotations.SerializedName

data class PriceTickerData(

    @SerializedName("E")
    val eventTime: Long,

    @SerializedName("e")
    val eventType: String,

    @SerializedName("s")
    val symbol: String,

    @SerializedName("c")
    val last: String,

    @SerializedName("h")
    val high: String,

    @SerializedName("o")
    val open: String,

    @SerializedName("l")
    val low: String,

    @SerializedName("p")
    val priceChange: String,

    @SerializedName("P")
    val priceChangePercent: String,

)