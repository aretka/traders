package com.example.traders.network.models.binanceCandleData

import android.util.Log

class BinanceCandleDataSublist : ArrayList<Any>() {
    //        0 = Open Time; 1 = Open; 2 = High, 3 = Low, 4 = Close, 5 = Volume, 6 = Close Time
    fun toCastedCandleData(): BinanceChartCandle {
        Log.d("BCDS", "toCastedCandleData: ${get(5).javaClass.kotlin.simpleName}")
        return BinanceChartCandle(
            open = (this.get(1) as? String)?.toFloat() ?: 0F,
            high = (this.get(2) as? String)?.toFloat() ?: 0F,
            low = (this.get(3) as? String)?.toFloat() ?: 0F,
            close = (this.get(4) as? String)?.toFloat() ?: 0F,
            volume = (this.get(5) as? String)?.toFloat() ?: 0F,
            date = (this.get(0) as? Double)?.toLong() ?: 0L
        )
    }
}
