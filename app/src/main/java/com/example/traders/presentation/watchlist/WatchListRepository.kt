package com.example.traders.presentation.watchlist

import com.example.traders.database.CryptoDatabaseDao
import com.example.traders.database.FavouriteCrypto
import com.example.traders.database.PreferancesManager
import com.example.traders.database.SortOrder
import com.example.traders.network.BinanceApi
import com.example.traders.network.repository.enumContains
import com.example.traders.utils.MappingUtils.enumConstantNames
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.binance24HourData.Binance24DataItem
import com.example.traders.network.models.binance24HourData.BinanceDataItem
import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import com.example.traders.network.webSocket.BinanceWSClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WatchListRepository @Inject constructor(
    private val webSocketClient: BinanceWSClient,
    private val binanceApi: BinanceApi,
    private val cryptoDao: CryptoDatabaseDao,
    private val preferencesManager: PreferancesManager
) {
    private val _binanceCryptoList = MutableStateFlow<List<BinanceDataItem>>(emptyList())
    val binanceCryptoList = _binanceCryptoList
        .combine(preferencesManager.preferencesFlow) { list, preferences ->
            if (preferences.isFavourite) {
                list.filter { it.isFavourite }
            } else {
                list
            }
        }

    private var webSocketJob: Job? = null

    suspend fun refreshCryptoPrices() {
        val cryptoPricesResponse = getCryptoPricesList() ?: emptyList()
        val extractedPricesList =
            cryptoPricesResponse.getFixedCryptoList().map {
                it.toBinanceDataItem()
            }

        val finalList = extractedPricesList.applyFavourites(getAllFavourites())
        _binanceCryptoList.update { finalList }
    }

    suspend fun renewListWithFavourites() {
        _binanceCryptoList.update {
            it.applyFavourites(getAllFavourites())
        }
    }

    fun sortList(sortOrder: SortOrder) {
        when (sortOrder) {
            SortOrder.DEFAULT -> emitDefaultList()
            SortOrder.BY_NAME_ASC -> emitSortedByNameAsc()
            SortOrder.BY_NAME_DESC -> emitSortedByNameDesc()
            SortOrder.BY_CHANGE_ASC -> emitSortedByChangeAsc()
            SortOrder.BY_CHANGE_DESC -> emitSortedByChangeDesc()
        }
    }

    private fun emitDefaultList() {
        val cryptoListOrder = FixedCryptoList::class.enumConstantNames().toMutableList()
        val orderBySymbol = cryptoListOrder.withIndex().associate { it.value to it.index }
        _binanceCryptoList.update { it.sortedBy { orderBySymbol[it.symbol] } }
    }

    private fun emitSortedByNameAsc() {
        _binanceCryptoList.update { list ->  list.sortedBy { it.symbol } }
    }

    private fun emitSortedByNameDesc() {
        _binanceCryptoList.update { list ->  list.sortedByDescending { it.symbol } }
    }

    private fun emitSortedByChangeAsc() {
        _binanceCryptoList.update { list ->  list.sortedBy { it.priceChangePercent.toBigDecimal() } }
    }

    private fun emitSortedByChangeDesc() {
        _binanceCryptoList.update { list ->  list.sortedByDescending { it.priceChangePercent.toBigDecimal() } }
    }

    suspend fun saveSortOrderOnPreference(sortOrder: SortOrder) {
        preferencesManager.updateSortOrder(sortOrder)
    }

    suspend fun saveIsFavouriteOnPreference(isFavouritesOn: Boolean) {
        preferencesManager.updateIsFavourite(isFavouritesOn)
    }

    suspend fun isFavouritesOn(): Boolean {
        return preferencesManager.preferencesFlow.first().isFavourite
    }

    suspend fun sortOrderType(): SortOrder {
        return preferencesManager.preferencesFlow.first().sortOrder
    }

    private fun List<Binance24DataItem>.getFixedCryptoList(): List<Binance24DataItem> {
        return filter { element ->
            enumContains<FixedCryptoList>(element.symbol.replace("USDT", ""))
        }
    }

    private suspend fun getCryptoPricesList() = binanceApi.get24HourData().body()

    private suspend fun getAllFavourites() = cryptoDao.getAllFavourites()

    private fun Binance24DataItem.toBinanceDataItem(): BinanceDataItem {
        return BinanceDataItem(
            symbol = symbol.replace("USDT", ""),
            last = last,
            high = high,
            low = low,
            open = open,
            priceChange = priceChange,
            priceChangePercent = priceChangePercent
        )
    }

    private fun List<BinanceDataItem>.applyFavourites(favouriteList: List<FavouriteCrypto>?): List<BinanceDataItem> {
        return map { item ->
            item.copy(
                isFavourite = favouriteList?.any { it.symbol == item.symbol } == true
            )
        }
    }

    //    I will have to start and pause collecting state
    // It collects message emitted from websocket sharedFlow and updates list item by reassigning BinanceDataItem to new value
    suspend fun startCollectingBinanceTickerData(scope: CoroutineScope) {
        webSocketJob = scope.launch {
            webSocketClient.state.collect { tickerData ->
                val indexOfCryptoDataToUpdate = _binanceCryptoList.value.indexOfFirst {
                    it.symbol == tickerData.symbol
                }

                if (indexOfCryptoDataToUpdate != -1) {
                    _binanceCryptoList.value = _binanceCryptoList.value.let {
                        val updatedList = it.toMutableList()
                        val itemToUpdate =
                            tickerData.toBinanceDataItem(updatedList[indexOfCryptoDataToUpdate].isFavourite)
                        if (itemToUpdate != null) {
                            updatedList[indexOfCryptoDataToUpdate] = itemToUpdate
                        }
                        updatedList
                    }
                }
            }
        }
    }

    fun stopCollectingBinanceTickerData() {
        webSocketJob?.cancel()
    }

    // Converts PriceTickerData tp Binance24DataItem
    private fun PriceTickerData?.toBinanceDataItem(isFavourite: Boolean): BinanceDataItem? {
        if (this == null) return null
        return BinanceDataItem(
            symbol = symbol.replace("USDT", ""),
            last = last,
            high = high,
            low = low,
            open = open,
            priceChange = priceChange,
            priceChangePercent = priceChangePercent,
            isFavourite = isFavourite
        )
    }
}