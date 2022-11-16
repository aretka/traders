package com.example.traders.presentation.cryptoDetailsScreen.chartTab

import com.example.traders.network.BinanceApi
import com.example.traders.network.models.binanceCandleData.BinanceCandleDataSublist
import com.example.traders.network.models.binanceCandleData.BinanceChartCandle
import com.example.traders.network.models.cryptoChartData.CryptoChartCandle
import com.example.traders.utils.DateUtils
import javax.inject.Inject

class ChartRepository @Inject constructor(
    private val binanceApi: BinanceApi
) {
    suspend fun getBinanceCandleData(
        symbol: String,
        candleType: CandleType
    ): List<CryptoChartCandle> {
        val response =
            binanceApi.getBinanceCandleData(symbol, candleType.candleInterval, candleType.limit)
        return response.body()?.map { cryptoCandle ->
            returnBinanceChartCandle(cryptoCandle)
        } ?: emptyList()
    }

    private fun returnBinanceChartCandle(cryptoCandle: BinanceCandleDataSublist): CryptoChartCandle {
        val binanceCandle = cryptoCandle.toCastedCandleData()
        return CryptoChartCandle(
            open = binanceCandle.open,
            high = binanceCandle.high,
            low = binanceCandle.low,
            close = binanceCandle.close,
            volume = binanceCandle.volume,
            priceChange = binancePriceChange(binanceCandle),
            percentPriceChange = binancePercentPriceChange(binanceCandle),
            date = DateUtils.getDateFromTimeStamp(binanceCandle.date)
        )
    }

    private fun binancePriceChange(binanceCandle: BinanceChartCandle) =
        binanceCandle.open - binanceCandle.close

    private fun binancePercentPriceChange(binanceCandle: BinanceChartCandle): Float {
        return 100 * (binanceCandle.close - binanceCandle.open) / binanceCandle.open
    }
}
