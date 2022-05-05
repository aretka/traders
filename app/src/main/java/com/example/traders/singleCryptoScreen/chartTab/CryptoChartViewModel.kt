package com.example.traders.singleCryptoScreen.chartTab

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.webSocket.BinanceWSClient
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
    fun fetchCryptoChartData(candleType: CandleType) {
        val startDate = getStartDate(candleType.numDays)

        launch {
            val response = repository.getCryptoChartData(crypto.slug, startDate, candleType.candleInterval).body()
                ?: return@launch

            when (candleType) {
                CandleType.DAILY -> _chartState.value =
                    _chartState.value.copy(chartDataFor90d = response.data.values)
                CandleType.WEEKLY -> _chartState.value =
                    _chartState.value.copy(chartDataFor360d = response.data.values)
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
            webSocketClient.state.collect {
                if (it.symbol.replace("USDT", "") == crypto.name.uppercase()) {
                    _chartState.value = _chartState.value.copy(tickerData = it)
                }
            }
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

