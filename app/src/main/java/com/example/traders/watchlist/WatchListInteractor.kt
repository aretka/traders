package com.example.traders.watchlist

import com.example.traders.database.CryptoDatabaseDao
import com.example.traders.database.PreferancesManager
import com.example.traders.network.BinanceApi
import javax.inject.Inject

class WatchListInteractor @Inject constructor(
    private val binanceApi: BinanceApi,
    private val cryptoDao: CryptoDatabaseDao,
    private val preferencesManager: PreferancesManager,
) {

    suspend fun getCryptoPricesList() = binanceApi.get24HourData().body()

    fun getAllFavourites() = cryptoDao.getAllFavourites().value
}
