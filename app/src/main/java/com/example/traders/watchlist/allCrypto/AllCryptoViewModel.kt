package com.example.traders.watchlist.allCrypto

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.traders.BaseViewModel
import com.example.traders.ToBinance24DataItem
import com.example.traders.repository.CryptoRepository
import com.example.traders.repository.enumContains
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.webSocket.BinanceWSClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import javax.inject.Inject

@HiltViewModel
class AllCryptoViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val webSocketClient: BinanceWSClient
) : BaseViewModel() {

    private val _state = MutableStateFlow(AllCryptoState())
    val state = _state.asStateFlow()

    init {
        getBinanceData()
        startCollectingBinanceTickerData()
    }

    // This function cannot be called since connection hasnt been established yet at this point
    // Subscribe and unsubscribe must be called when connection is successfully established or terminated
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
            _state.update { it.copy(isCryptoFetched = true) }
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    private suspend fun updateCryptoData() {
        val cryptoPrices = repository.getBinance24Data().body() ?: return
        val extractedPricesList = cryptoPrices.filter { el -> enumContains<FixedCryptoList>(el.symbol.replace("USDT", "")) }
        _state.value = _state.value?.copy(binanceCryptoData = extractedPricesList)
    }

    // It collects message emitted from websocket sharedFlow and updates list item by reassigning BinanceDataItem to new value
    private fun startCollectingBinanceTickerData() {
        launch {
            webSocketClient.state.collect { tickerData ->
                val indexOfCryptoDataToUpdate = _state.value.binanceCryptoData.indexOfFirst {
                    it.symbol == tickerData.data?.symbol
                }

                _state.value = _state.value.let {
                    val updatedList = it.binanceCryptoData.toMutableList()
                    val itemToUpdate = tickerData.data.ToBinance24DataItem()
                    if (itemToUpdate != null) {
                        updatedList[indexOfCryptoDataToUpdate] = itemToUpdate
                    }
                    it.copy(binanceCryptoData = updatedList)
                }
            }
        }
    }

}
