package com.example.traders.repository

import android.content.SharedPreferences
import android.util.Log
import com.example.traders.database.Crypto
import com.example.traders.database.CryptoDatabaseDao
import com.example.traders.network.BinanceApi
import com.example.traders.network.MessariApi
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoRepository @Inject constructor(
    private val api: MessariApi,
    private val binanceApi: BinanceApi,
    private val sharedPrefs: SharedPreferences,
    private val cryptoDao: CryptoDatabaseDao
) {

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

    // Messari api
    suspend fun getCryptoPrices() = api.getCryptoPrices()
    suspend fun getCryptoPriceStatistics(slug: String) = api.getCryptoPriceStatistics(slug)
    suspend fun getCryptoChartData(
        slug: String,
        afterDate: String,
        interval: String
    ) = api.getCryptoChartData(slug, afterDate, interval)
    suspend fun getCryptoDescriptionData(id: String) = api.getCryptoDescriptionData(id)

    // Binance api
    suspend fun checkServerTime() = binanceApi.checkServerTime()
    suspend fun getBinance24Data() = binanceApi.get24HourData()
    suspend fun getBinanceTickerBySymbol(symbol: String) = binanceApi.getBinanceTickerBySymbol(symbol)

    // Shared preferences
    fun getStoredTag(symbol: String) = sharedPrefs.getFloat(symbol, 0F)
    fun setStoredPrice(symbol: String, newPrice: Float) {
        sharedPrefs.edit().putFloat(symbol, newPrice).apply()
    }

    // Room Database
    fun getLiveAllCryptoPortfolio() = cryptoDao.getAllCryptoLive()
    suspend fun getAllCryptoPortfolio() = cryptoDao.getAllCrypto()
    suspend fun insertCrypto(crypto: Crypto) = cryptoDao.insertCrypto(crypto)
    suspend fun deleteCrypto(crypto: Crypto) = cryptoDao.deleteCrypto(crypto)
    suspend fun getCryptoBySymbol(symbol: String) = cryptoDao.getCryptoBySymbol(symbol)
    suspend fun deleteAllCryptoFromDb() = cryptoDao.deleteAllCryptoFromDb()

    companion object {
        const val REFRESH_INTERVAL_MS = 10000L
    }
}

inline fun <reified T : Enum<T>> enumContains(symbol: String): Boolean {
    return enumValues<T>().any { it.name == symbol}
}