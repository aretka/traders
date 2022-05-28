package com.example.traders.network.models.binance24hTickerData

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.traders.network.models.cryptoChartData.CryptoChartCandle
import com.example.traders.utils.DateUtils.getCandleDate
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun toCryptoChartCandle(): CryptoChartCandle {
        return CryptoChartCandle(
            close = last.toFloat(),
            priceChange = priceChange.toFloat(),
            percentPriceChange = priceChangePercent.toFloat(),
            date = getCandleDate(0L)
        )
    }
}
