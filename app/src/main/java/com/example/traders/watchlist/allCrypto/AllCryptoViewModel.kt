package com.example.traders.watchlist.allCrypto

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.traders.BaseViewModel
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

    //TODO I will transfer to StateFlow later, this is just for easier personal usage
    private val _cryptoData = MutableLiveData(AllCryptoState())
    val cryptoData
        get() = _cryptoData

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing
        get() = _isRefreshing

    init {
//        getCrypto()
        repository.startFetching()
        collectBinanceData()
        collectBinanceTickerData()
    }

    // This function cannot be called since connection hasnt been established yet at this point
    // Subscribe and unsubscribe will be called when connection is successfully established
    private fun initWebSocket() {
        Log.e("ALlCryptoViewModel", "initWebSocket called")
        webSocketClient.subscribe(listOf("btcusdt", "bnbusdt"), "ticker")
    }

    private fun collectBinanceData() {
        launch {
            repository.binanceMarketData.collect { list ->
                if (list != null) {
                    _cryptoData.value = _cryptoData.value?.copy(binanceCryptoData = list)
                }
            }
        }
    }

    private fun collectBinanceTickerData() {
        Log.e("AllCryptoView", "CollectBinanceTickerData launched")
        launch {
            webSocketClient.state.collect {
                Log.e("AllCryptoView", it.data?.last.orEmpty())
            }
        }
    }

    private fun getCrypto() {
        launchWithProgress {
            updateCryptoData()
        }
    }

    fun getCryptoOnRefresh() {
        launch {
            _isRefreshing.value = true
            updateCryptoData()
            _isRefreshing.value = false
        }
    }

    private suspend fun updateCryptoData() {
        val cryptoPrices = repository.getCryptoPrices().body()?.data ?: return
        _cryptoData.value = _cryptoData.value?.copy(cryptoList = cryptoPrices)
    }

}
