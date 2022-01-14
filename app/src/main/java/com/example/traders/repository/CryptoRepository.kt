package com.example.traders.repository

import com.example.traders.network.BinanceApi
import com.example.traders.network.MessariApi
import javax.inject.Inject

//@Module
//@InstallIn(ActivityRetainedComponent::class)
class CryptoRepository @Inject constructor(private val api: MessariApi, private val binanceApi: BinanceApi) {
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
}