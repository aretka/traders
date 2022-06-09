package com.example.traders.presentation.cryptoDetailsScreen.chartTab

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.cryptoChartData.CryptoChartCandle
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.network.webSocket.BinanceWSClient
import com.example.traders.presentation.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class CryptoChartViewModel @AssistedInject constructor(
    private val repository: ChartRepository,
    private val webSocketClient: BinanceWSClient,
    @Assisted private val crypto: FixedCryptoList
) : BaseViewModel() {

    private val _chartState = MutableStateFlow(ChartState())
    val chartState = _chartState.asStateFlow()

    init {
        fetchCandleData()
        collectBinanceTickerData()
    }

    fun onChartBtnSelected(btnId: BtnId) {
        if (_chartState.value.chartBtnsEnabled) {
            _chartState.value = _chartState.value.copy(
                prevActiveButtonId = _chartState.value.activeButtonId,
                activeButtonId = btnId
            )
        }
    }

    fun onChartLongPressClick(cryptoCandle: CryptoChartCandle?) {
        if (cryptoCandle != null) {
            _chartState.update {
                it.copy(
                    tickerData = cryptoCandle,
                    showChartPrice = true
                )
            }
        } else {
            _chartState.update { it.copy(
                showChartPrice = false,
                tickerData = it.latestCryptoTickerPrice
            ) }
        }
    }

    private fun fetchCandleData() {
        launch {
            val candleFutureData = listOf(
                async { fetchCryptoChartData(CandleType.DAILY) },
                async { fetchCryptoChartData(CandleType.WEEKLY) }
            )
            candleFutureData.awaitAll()
            _chartState.update { it.copy(chartBtnsEnabled = true) }
        }
    }

    private fun collectBinanceTickerData() {
        launch {
            webSocketClient.state.collect { ticker ->
                if (ticker.symbol == crypto.name && !_chartState.value.showChartPrice) {
                    val updatedTickerPrice = ticker.toCryptoChartCandle()
                    _chartState.update { it.copy(
                        tickerData = updatedTickerPrice,
                        latestCryptoTickerPrice = updatedTickerPrice
                    ) }
                }
            }
        }
    }

    private suspend fun fetchCryptoChartData(candleType: CandleType) {
        val symbol = crypto.name.uppercase() + "USDT"
        val list = repository.getBinanceCandleData(symbol, candleType)
        when (candleType) {
            CandleType.DAILY -> {
                _chartState.value = _chartState.value.copy(
                    chartCandleDataFor90D = list,
                )
            }
            CandleType.WEEKLY -> {
                _chartState.value = _chartState.value.copy(
                    chartCandleDataFor360D = list,
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(crypto: FixedCryptoList): CryptoChartViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            crypto: FixedCryptoList
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(crypto) as T
            }
        }
    }
}

enum class BtnId {
    MONTH1_BTN, MONTH3_BTN, MONTH6_BTN, MONTH12_BTN
}

