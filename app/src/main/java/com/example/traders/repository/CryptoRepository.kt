package com.example.traders.repository

import com.example.traders.database.*
import com.example.traders.network.BinanceApi
import com.example.traders.network.MessariApi
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CryptoRepository @Inject constructor(
    private val api: MessariApi,
    private val binanceApi: BinanceApi,
    private val cryptoDao: CryptoDatabaseDao
) {

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
    suspend fun getBinance24Data() = binanceApi.get24HourData()
    suspend fun getBinanceTickerBySymbol(symbol: String) =
        binanceApi.getBinanceTickerBySymbol(symbol)

    // Room Database
    fun getLiveAllCryptoPortfolio() = cryptoDao.getAllCryptoLive()
    suspend fun getAllCryptoPortfolio() = cryptoDao.getAllCrypto()
    suspend fun insertCrypto(crypto: Crypto) = cryptoDao.insertCrypto(crypto)
    suspend fun deleteCrypto(crypto: Crypto) = cryptoDao.deleteCrypto(crypto)
    suspend fun getCryptoBySymbol(symbol: String) = cryptoDao.getCryptoBySymbol(symbol)
    suspend fun deleteAllCryptoFromDb() = cryptoDao.deleteAllCryptoFromDb()
    suspend fun updateCrypto(amount: BigDecimal, symbol: String) =
        cryptoDao.updateCrypto(amount, symbol)

    suspend fun insertTransaction(
        symbol: String,
        amount: BigDecimal,
        usdAmount: BigDecimal = BigDecimal(0),
        lastPrice: BigDecimal = BigDecimal(0),
        time: String,
        transactionType: TransactionType
    ) = cryptoDao.insertTransaction(
        Transaction(
            symbol = symbol,
            amount = amount,
            usdAmount = usdAmount,
            lastPrice = lastPrice,
            time = time,
            transactionType = transactionType
        )
    )

    suspend fun deleteAllTransactions() = cryptoDao.deleteAllTransactions()
    fun getAllTransactionsLive() = cryptoDao.getAllTransactionsLive()
    fun getAllFavourites() = cryptoDao.getAllFavourites()
    suspend fun insertFavouriteCrypto(favouriteCrypto: FavouriteCrypto) =
        cryptoDao.insertFavouriteCrypto(favouriteCrypto)

    suspend fun deleteFavouriteCrypto(symbol: String) =
        cryptoDao.deleteFavouriteCrypto(symbol)

    suspend fun getFavouriteBySymbol(symbol: String) = cryptoDao.getFavouriteBySymbol(symbol)
}

inline fun <reified T : Enum<T>> enumContains(symbol: String): Boolean {
    return enumValues<T>().any { it.name == symbol }
}