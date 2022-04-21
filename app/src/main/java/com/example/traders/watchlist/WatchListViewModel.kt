package com.example.traders.watchlist

import android.util.Log
import com.example.traders.BaseViewModel
import com.example.traders.database.FavouriteCrypto
import com.example.traders.repository.CryptoRepository
import com.example.traders.repository.enumContains
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24HourData.BinanceDataItem
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import com.example.traders.webSocket.BinanceWSClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val webSocketClient: BinanceWSClient
): BaseViewModel() {
    private val _state = MutableStateFlow(WatchListState())
    val state = _state.asStateFlow()

    val favouriteCryptoList = repository.getAllFavourites()

    init {
        getBinanceData()
        startCollectingBinanceTickerData()
    }

//    Updates binanceDataList which is stored in a state
    fun onNewFavouriteList() {
        if(_state.value.binanceCryptoData.isNotEmpty()) {
            val newBinanceItemList = _state.value.binanceCryptoData
                .toListWithFavourites(favouriteCryptoList.value)
            _state.update { it.copy(binanceCryptoData = newBinanceItemList) }
        }
    }

    // This function cannot be called since connection hasnt been established yet at this point
    // Subscribe and unsubscribe must be called when connection is successfully established and terminated respectively
    private fun subscribeWebSocket() {
        Log.e("ALlCryptoViewModel", "initWebSocket called")
        webSocketClient.subscribe(listOf("btcusdt", "bnbusdt"), "ticker")
    }

    private fun getBinanceData() {
        launchWithProgress {
            updateCryptoData()
        }
    }

    fun getCryptoOnRefresh() {
        launch {
            _state.update { it.copy(isRefreshing = true) }
            updateCryptoData()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun updateCryptoData() {
        val cryptoPrices = repository.getBinance24Data().body() ?: return
        val extractedPricesList = cryptoPrices.filter { el ->
            enumContains<FixedCryptoList>(el.symbol.replace("USDT", ""))
        }.map { it.toBinanceDataItem() }

        val listWithFavouriteCrypto = extractedPricesList.toListWithFavourites(favouriteCryptoList.value)

        _state.value = _state.value.copy(binanceCryptoData = listWithFavouriteCrypto)
    }

    // It collects message emitted from websocket sharedFlow and updates list item by reassigning BinanceDataItem to new value
    private fun startCollectingBinanceTickerData() {
        launch {
            webSocketClient.state.collectLatest { tickerData ->
                val indexOfCryptoDataToUpdate = _state.value.binanceCryptoData.indexOfFirst {
                    it.symbol == tickerData.symbol
                }

                _state.value = _state.value.let {
                    val updatedList = it.binanceCryptoData.toMutableList()
                    val itemToUpdate = tickerData.toBinanceDataItem(updatedList[indexOfCryptoDataToUpdate].isFavourite)
                    if (itemToUpdate != null) {
                        updatedList[indexOfCryptoDataToUpdate] = itemToUpdate
                    }
                    it.copy(binanceCryptoData = updatedList)
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

private fun List<BinanceDataItem>.toListWithFavourites(favouriteList: List<FavouriteCrypto>?): List<BinanceDataItem> {
    return this.map { binanceDataItem ->
        favouriteList?.filter { favouriteCrypto ->
            favouriteCrypto.symbol == binanceDataItem.symbol.replace("USDT", "")
        }?.let {
            binanceDataItem.copy(isFavourite = it.isNotEmpty())
        } ?: binanceDataItem
    }
}

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
