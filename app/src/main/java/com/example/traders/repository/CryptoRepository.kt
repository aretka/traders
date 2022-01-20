package com.example.traders.repository

import android.util.Log
import com.example.traders.network.BinanceApi
import com.example.traders.network.MessariApi
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onErrorResume
import javax.inject.Inject

//@Module
//@InstallIn(ActivityRetainedComponent::class)

class CryptoRepository @Inject constructor(private val api: MessariApi, private val binanceApi: BinanceApi) {

    var isFetching = false

    val binanceMarketData = flow {
        while (isFetching) {
            val response = getBinance24Data()
            val extractedList = response.body()?.filter { el -> enumContains<FixedCryptoList>(el.symbol.replace("USDT", ""))}
            emit(extractedList)
            delay(REFRESH_INTERVAL_MS)
        }
    }.catch { e -> Log.e("CryptoRepository", "Binance 24h data request failed", e) }

    fun startFetching() {
        isFetching = true
    }

    fun stopFetching() {
        isFetching = false
    }

    suspend fun getCryptoPrices() = api.getCryptoPrices()
    suspend fun getCryptoPriceStatistics(slug: String) = api.getCryptoPriceStatistics(slug)
    suspend fun getCryptoChartData(
        slug: String,
        afterDate: String,
        interval: String
    ) = api.getCryptoChartData(slug, afterDate, interval)
    suspend fun getCryptoDescriptionData(id: String) = api.getCryptoDescriptionData(id)
    suspend fun checkServerTime() = binanceApi.checkServerTime()
    suspend fun getBinance24Data() = binanceApi.get24HourData()

    companion object {
        const val REFRESH_INTERVAL_MS = 10000L
    }
}

inline fun <reified T : Enum<T>> enumContains(symbol: String): Boolean {
    return enumValues<T>().any { it.name == symbol}
}