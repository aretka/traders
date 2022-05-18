package com.example.traders.network.models.binance24hTickerData

import com.example.traders.network.models.cryptoChartData.CryptoChart
import com.google.gson.annotations.SerializedName

data class PriceTickerData(

    @SerializedName("s")
    val symbol: String = "",

    @SerializedName("c")
    val last: String = "",

    @SerializedName("h")
    val high: String = "",

    @SerializedName("l")
    val low: String = "",

    @SerializedName("o")
    val open: String = "",

    @SerializedName("p")
    val priceChange: String = "",

    @SerializedName("P")
    val priceChangePercent: String = "",

) {
    fun toCryptoChart(): CryptoChart {
        return CryptoChart(
            close = last.toFloat(),
            priceChange = priceChange.toFloat(),
            percentPriceChange = priceChangePercent.toFloat()
        )
    }
}
