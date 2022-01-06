package com.example.traders.repository

import com.example.traders.network.CryptoApi
import retrofit2.http.Path
import javax.inject.Inject

//@Module
//@InstallIn(ActivityRetainedComponent::class)
class CryptoRepository @Inject constructor(private val api: CryptoApi) {
    suspend fun getCryptoPrices() = api.getCryptoPrices()
    suspend fun getCryptoPriceStatistics(slug: String) = api.getCryptoPriceStatistics(slug)
    suspend fun getCryptoChartData(
        slug: String,
        startDate: String,
        endDate: String,
        interval: String
    ) = api.getCryptoChartData(slug, startDate, endDate, interval)
}