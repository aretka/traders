package com.example.traders.repository

import com.example.traders.network.CryptoApi
import javax.inject.Inject

//@Module
//@InstallIn(ActivityRetainedComponent::class)
class CryptoRepository @Inject constructor(private val api: CryptoApi) {
    suspend fun getCryptoPrices() = api.getCryptoPrices()
    suspend fun getCryptoPriceStatistics(slug: String) = api.getCryptoPriceStatistics(slug)
}