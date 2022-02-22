package com.example.traders.watchlist.singleCryptoScreen.chartTab

import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData

data class ChartState(
    val chartDataFor90d: List<List<Float>>? = null,
    val chartDataFor360d: List<List<Float>>? = null,
    val tickerData: PriceTickerData? = null,
    val chartBtnsEnabled: Boolean = false,
    val isMonth1BtnActive: Boolean = true,
    val isMonth3BtnActive: Boolean = false,
    val isMonth6BtnActive: Boolean = false,
    val isMonth12BtnActive: Boolean = false,
    val activeButtonId: BtnId = BtnId.MONTH1_BTN,
    val prevActiveButtonId: BtnId? = null,
    val dialogBtnsEnabled: Boolean = false
)
