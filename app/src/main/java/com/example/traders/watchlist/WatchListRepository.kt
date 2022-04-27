package com.example.traders.watchlist

import android.content.Context
import com.example.traders.database.FavouriteCrypto
import com.example.traders.hilt.ApplicationScopeDefault
import com.example.traders.repository.enumContains
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24HourData.BinanceDataItem
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import com.example.traders.webSocket.BinanceWSClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchListRepository @Inject constructor(
    private val watchListInteractor: WatchListInteractor,
    private val webSocketClient: BinanceWSClient,
    @ApplicationScopeDefault val scope: CoroutineScope
) {
//    TODO: start collecting ticker data at first refreshCryptoPrices call
//
    private val _binanceCryptoList = MutableStateFlow<List<BinanceDataItem>>(emptyList())
    val binanceCryptoList = _binanceCryptoList.asStateFlow()

    var cryptoPrices: List<BinanceDataItem> = emptyList()
        private set

    suspend fun refreshCryptoPrices() {
        val cryptoPricesResponse = watchListInteractor.getCryptoPricesList() ?: emptyList()
        val extractedPricesList =
            cryptoPricesResponse.getFixedCryptoList().map { it.toBinanceDataItem() }

        cryptoPrices = extractedPricesList.applyFavourites(getFavouriteCryptos())
    }

    suspend fun getFavourites(): List<BinanceDataItem> {
        return cryptoPrices.filter { it.isFavourite }
    }

    private fun List<Binance24DataItem>.getFixedCryptoList(): List<Binance24DataItem> {
        return filter { element ->
            enumContains<FixedCryptoList>(element.symbol.replace("USDT", ""))
        }
    }

    private fun getFavouriteCryptos() = watchListInteractor.getAllFavourites()

    private fun Binance24DataItem.toBinanceDataItem(): BinanceDataItem {
        return BinanceDataItem(
            symbol = symbol,
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
                isFavourite = favouriteList?.any {
                    it.symbol == item.symbol.replace(
                        "USDT",
                        ""
                    )
                } == true
            )
        }
    }

//    I will have to start and pause collecting state
    // It collects message emitted from websocket sharedFlow and updates list item by reassigning BinanceDataItem to new value
    private fun startCollectingBinanceTickerData() {
        scope.launch {
            webSocketClient.state.collectLatest { tickerData ->
                val indexOfCryptoDataToUpdate = _binanceCryptoList.value.indexOfFirst {
                    it.symbol == tickerData.symbol
                }

                if(indexOfCryptoDataToUpdate != -1) {
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

    // Converts PriceTickerData tp Binance24DataItem
    private fun PriceTickerData?.toBinanceDataItem(isFavourite: Boolean): BinanceDataItem? {
        if (this == null) return null
        return BinanceDataItem(
            symbol = symbol,
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
