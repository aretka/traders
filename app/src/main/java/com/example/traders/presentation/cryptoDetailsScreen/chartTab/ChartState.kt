package com.example.traders.presentation.cryptoDetailsScreen.chartTab

import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import com.github.mikephil.charting.data.Entry

data class ChartState(
    val chartDataFor90d: List<List<Float>>? = null,
    val chartDataFor360d: List<List<Float>>? = null,
    val lineChartData90d: List<Float> = emptyList(),
    val lineChartData360d: List<Float> = emptyList(),
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
