package com.example.traders.presentation.cryptoDetailsScreen.chartTab

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.presentation.BaseViewModel
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.cryptoChartData.CryptoChart
import com.example.traders.network.webSocket.BinanceWSClient
import com.github.mikephil.charting.data.Entry
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CryptoChartViewModel @AssistedInject constructor(
    private val repository: CryptoRepository,
    private val webSocketClient: BinanceWSClient,
    @Assisted private val crypto: FixedCryptoList
) : BaseViewModel() {

    private val _chartState = MutableStateFlow(ChartState())
    val chartState = _chartState.asStateFlow()

    init {
        collectBinanceTickerData()
        fetchCryptoChartData(CandleType.DAILY)
        fetchCryptoChartData(CandleType.WEEKLY)
    }

    fun onChartBtnSelected(btnId: BtnId) {
        if (_chartState.value.chartBtnsEnabled) {
            _chartState.value = _chartState.value.copy(
                prevActiveButtonId = _chartState.value.activeButtonId,
                activeButtonId = btnId
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchCryptoChartData(candleType: CandleType) {
        val startDate = getStartDate(candleType.numDays)

        launch {
            val list = repository.getCryptoChartData(crypto.slug, startDate, candleType.candleInterval)
            when (candleType) {
                CandleType.DAILY -> {
                    _chartState.value = _chartState.value.copy(
                        chartDataFor90d = list,
                    )
                }
                CandleType.WEEKLY -> {
                    _chartState.value = _chartState.value.copy(
                        chartDataFor360d = list,
                    )
                }
            }

            if (chartDataIsFetched()) {
                _chartState.value = _chartState.value.copy(chartBtnsEnabled = true)
            }
        }
    }

    private fun chartDataIsFetched(): Boolean {
        return _chartState.value.chartDataFor90d != null && _chartState.value.chartDataFor360d != null
    }

    fun collectBinanceTickerData() {
        launch {
            webSocketClient.state.collect { ticker ->
                if (ticker.symbol.replace("USDT", "") == crypto.name.uppercase()) {
                    if(!_chartState.value.showChartPrice) {
                        _chartState.value = _chartState.value.copy(tickerData = ticker.toCryptoChart())
                    }
                }
            }
        }
    }

    fun onChartLongPressClick(crypto: CryptoChart?) {
        if(crypto != null) {
            _chartState.update { it.copy(
                tickerData = crypto,
                showChartPrice = true
            ) }
        } else {
            _chartState.update { it.copy(showChartPrice = false) }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartDate(daysBefore: Long): String {
        // ISO_LOCAL_DATE formats date to uuuu-mm-dd
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val date: LocalDate = LocalDate.now().minusDays(daysBefore)
        val text: String = date.format(formatter)
        return text
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

