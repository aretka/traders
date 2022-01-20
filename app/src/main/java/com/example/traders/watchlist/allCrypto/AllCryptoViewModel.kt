package com.example.traders.watchlist.allCrypto

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.traders.BaseViewModel
import com.example.traders.ToBinance24DataItem
import com.example.traders.repository.CryptoRepository
import com.example.traders.webSocket.BinanceWSClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
        repository.startFetching()
        collectBinanceData()
        collectBinanceTickerData()
    }

    // This function cannot be called since connection hasnt been established yet at this point
    // Subscribe and unsubscribe will be called when connection is successfully established or terminated
    private fun initWebSocket() {
        Log.e("ALlCryptoViewModel", "initWebSocket called")
        webSocketClient.subscribe(listOf("btcusdt", "bnbusdt"), "ticker")
    }

    private fun collectBinanceData() {
        launch {
            repository.binanceMarketData.collect { list ->
                if (list != null) {
                    _state.value = _state.value?.copy(binanceCryptoData = list)
                }
            }
        }
    }

    private fun collectBinanceTickerData() {
        Log.e("AllCryptoView", "CollectBinanceTickerData launched")
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
                Log.e("AllCryptoView", tickerData.data?.last.orEmpty())
            }
        }
    }

    // TODO remove this when binance websockets fully implemented
    private fun getCrypto() {
        launchWithProgress {
            updateCryptoData()
        }
    }

    fun getCryptoOnRefresh() {
        launch {
            _state.value = _state.value?.copy(isRefreshing = true)
            updateCryptoData()
            _state.value = _state.value?.copy(isCryptoFetched = true)
            _state.value = _state.value?.copy(isRefreshing = false)
        }
    }

    private suspend fun updateCryptoData() {
        val cryptoPrices = repository.getCryptoPrices().body()?.data ?: return
        _state.value = _state.value?.copy(cryptoList = cryptoPrices)
    }

}
