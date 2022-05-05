package com.example.traders.network.webSocket

import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import kotlinx.coroutines.flow.SharedFlow

interface BinanceWSClient {
    val state: SharedFlow<PriceTickerData>
    fun subscribe(params: List<String>, type: String)
    fun unsubscribe(params: List<String>, type: String)
    fun startConnection()
    fun stopConnection()
    fun restartConnection()
}

