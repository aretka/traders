package com.example.traders.watchlist

import android.util.Log
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.repository.enumContains
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import com.example.traders.webSocket.BinanceWSClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val webSocketClient: BinanceWSClient
): BaseViewModel() {
    private val _state = MutableStateFlow(WatchListState())
    val state = _state.asStateFlow()

    init {
        getBinanceData()
        startCollectingBinanceTickerData()
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
        }
//        _state.update { it.copy(binanceCryptoData = extractedPricesList) }
        _state.value = _state.value.copy(binanceCryptoData = extractedPricesList)
//        TODO: edit binance CryptoData to a new one with isFavourite field
//        TODO: map list to new list using favourites

//        TODO: start collecting list and map new list to a new one map
    }

    // It collects message emitted from websocket sharedFlow and updates list item by reassigning BinanceDataItem to new value
    private fun startCollectingBinanceTickerData() {
        launch {
            webSocketClient.state.collect { tickerData ->
                val indexOfCryptoDataToUpdate = _state.value.binanceCryptoData.indexOfFirst {
                    it.symbol == tickerData.symbol
                }

                _state.value = _state.value.let {
                    val updatedList = it.binanceCryptoData.toMutableList()
                    val itemToUpdate = tickerData.ToBinance24DataItem()
                    if (itemToUpdate != null) {
                        updatedList[indexOfCryptoDataToUpdate] = itemToUpdate
                    }
                    it.copy(binanceCryptoData = updatedList)
                }
            }
        }
    }

    // Converts PriceTickerData tp Binance24DataItem
    private fun PriceTickerData?.ToBinance24DataItem(): Binance24DataItem? {
        if (this == null) return null
        return Binance24DataItem(
            symbol = symbol,
            last = last,
            high = high,
            low = low,
            open = open,
            priceChange = priceChange,
            priceChangePercent = priceChangePercent
        )
    }
}