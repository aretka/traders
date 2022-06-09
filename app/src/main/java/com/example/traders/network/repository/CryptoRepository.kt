package com.example.traders.network.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.traders.database.*
import com.example.traders.network.BinanceApi
import com.example.traders.network.MessariApi
import com.example.traders.network.models.binanceCandleData.BinanceCandleDataSublist
import com.example.traders.network.models.cryptoChartData.CryptoChartCandle
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.CandleType
import com.example.traders.utils.DateUtils.getCandleDate
import com.example.traders.utils.DateUtils.getDateFromTimeStamp
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
    fun getAllFavouritesLive() = cryptoDao.getAllFavouritesLive()
    suspend fun insertFavouriteCrypto(favouriteCrypto: FavouriteCrypto) = cryptoDao.insertFavouriteCrypto(favouriteCrypto)
    suspend fun deleteFavouriteCrypto(symbol: String) = cryptoDao.deleteFavouriteCrypto(symbol)
    suspend fun getFavouriteBySymbol(symbol: String) = cryptoDao.getFavouriteBySymbol(symbol)

}

inline fun <reified T : Enum<T>> enumContains(symbol: String): Boolean {
    return enumValues<T>().any { it.name == symbol }
}