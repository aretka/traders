package com.example.traders.network.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.traders.database.*
import com.example.traders.network.BinanceApi
import com.example.traders.network.MessariApi
import com.example.traders.network.models.cryptoChartData.CryptoChartCandle
import com.example.traders.presentation.cryptoDetailsScreen.chartTab.CandleType
import com.example.traders.utils.DateUtils.getCandleDate
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
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getCryptoChartData(
        slug: String,
        candleType: CandleType
    ) : List<CryptoChartCandle> {
        val afterDate = getCandleDate(candleType.numDays)
        val response = api.getCryptoChartData(slug, afterDate, candleType.candleInterval)
        return if(response.body() != null) {
            response.body()?.data?.values?.mapIndexed { index, crypto ->
                // it = [volume, open, high, low, close]
//                returnCryptoChart()
                CryptoChartCandle(
                volume = crypto[0],
                open = crypto[1],
                high = crypto[2],
                low = crypto[3],
                close = crypto[4],
                priceChange = priceChange(crypto),
                percentPriceChange = percentPriceChange(crypto),
                date = getCandleDate(daysBefore(index, candleType, response.body()!!.data.values.size))
            ) } ?: emptyList()
        } else {
            emptyList()
        }
    }

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

    private fun priceChange(crypto: List<Float>) = crypto[1] - crypto[4]

    private fun percentPriceChange(crypto: List<Float>) = 100*(crypto[4] - crypto[1])/crypto[1]

    private fun daysBefore(index: Int, candleType: CandleType, size: Int): Long {
        return when(candleType) {
            CandleType.DAILY -> size.toLong() - index - 1
            CandleType.WEEKLY -> (size.toLong() - index - 1) * 7
        }
    }

//    private fun returnCryptoChart(): CryptoChart {
//        return CryptoChart()
//    }
}

inline fun <reified T : Enum<T>> enumContains(symbol: String): Boolean {
    return enumValues<T>().any { it.name == symbol }
}