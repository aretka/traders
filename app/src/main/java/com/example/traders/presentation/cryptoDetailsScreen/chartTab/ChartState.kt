package com.example.traders.presentation.cryptoDetailsScreen.chartTab

import com.example.traders.network.models.cryptoChartData.CryptoChartCandle

data class ChartState(
    val chartCandleDataFor90D: List<CryptoChartCandle>? = null,
    val chartCandleDataFor360D: List<CryptoChartCandle>? = null,
    val lineChartData90d: List<Float> = emptyList(),
    val lineChartData360d: List<Float> = emptyList(),
    val mainTickerData: CryptoChartCandle? = null,
    val chartBtnsEnabled: Boolean = false,
    val isMonth1BtnActive: Boolean = true,
    val isMonth3BtnActive: Boolean = false,
    val isMonth6BtnActive: Boolean = false,
    val isMonth12BtnActive: Boolean = false,
    val activeButtonId: BtnId = BtnId.MONTH1_BTN,
    val prevActiveButtonId: BtnId? = null,
    val dialogBtnsEnabled: Boolean = false,
    val showChartPrice: Boolean = false,
    val latestCryptoTickerPrice: CryptoChartCandle? = null
)
