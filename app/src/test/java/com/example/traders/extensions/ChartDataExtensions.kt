package com.example.traders.extensions

import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import com.example.traders.network.models.cryptoChartData.CryptoChartCandle

private const val PRICE_TICKER_SYMBOL = "BTC"
private const val PRICE_TICKER_LAST = "20"
private const val PRICE_TICKER_HIGH = "40.5"
private const val PRICE_TICKER_LOW = "15.5"
private const val PRICE_TICKER_OPEN = "17"
private const val PRICE_TICKER_CHANGE = "3"
private const val CHART_CANDLE_PERCENT_CHANGE = 12.2F
private const val CHART_CANDLE_PRICE_CHANGE = -3.5F
private const val CHART_CANDLE_DATE = "2022-09-08"

object ChartDataExtensions {
    internal fun createPriceTicker(): PriceTickerData {
        return PriceTickerData(
            symbol = PRICE_TICKER_SYMBOL,
            last = PRICE_TICKER_LAST,
            high = PRICE_TICKER_HIGH,
            low = PRICE_TICKER_LOW,
            open = PRICE_TICKER_OPEN,
            priceChange = PRICE_TICKER_CHANGE,
        )
    }

    internal fun createChartCandleResponse(): List<CryptoChartCandle> = List(10) {
        createChartCandle()
    }

    internal fun createChartCandle(): CryptoChartCandle = CryptoChartCandle(
        priceChange = CHART_CANDLE_PRICE_CHANGE,
        percentPriceChange = CHART_CANDLE_PERCENT_CHANGE,
        date = CHART_CANDLE_DATE
    )
}
