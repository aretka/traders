package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.webSocket.BinanceWSClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CryptoChartViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val webSocketClient: BinanceWSClient
) : BaseViewModel() {
    private var symbol: String = ""
    private var slug: String = ""
    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState>
        get() = _chartState

    init {
        collectBinanceTickerData()
    }

    fun fetchCryptoPriceStatistics(numDays: Long, candleInterval: String) {
        val startDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getStartDate(numDays)
        } else { TODO("VERSION.SDK_INT < O") }

        launch {
            val response = repository.getCryptoChartData(slug, startDate, candleInterval).body() ?: return@launch

            when (numDays.toInt()) {
                90 -> _chartState.value =
                    _chartState.value.copy(chartDataFor90d = response.data.values)
                360 -> _chartState.value =
                    _chartState.value.copy(chartDataFor360d = response.data.values)
                else -> Log.e(this.javaClass.toString(), "Wrong num of days")
            }

            if (_chartState.value.chartDataFor90d != null && _chartState.value.chartDataFor360d != null) {
                _chartState.value = _chartState.value.copy(chartBtnsEnabled = true)
            }
        }
    }

    fun collectBinanceTickerData() {
        launch {
            webSocketClient.state.collect {
                if( it.data?.symbol?.replace("USDT", "") == symbol.uppercase()) {
                    _chartState.value = _chartState.value.copy(tickerData = it)
                }
            }
        }
    }

    fun fetchAllChartData() {
        fetchCryptoPriceStatistics(90, "1d")
        fetchCryptoPriceStatistics(360, "1w")
    }

    fun onChartBtnSelected(btnId: BtnId) {
        if (_chartState.value.chartBtnsEnabled) {
            when (btnId) {
                BtnId.MONTH1_BTN -> {
                    setBtnActiveToFalse()
                    _chartState.value = _chartState.value.copy(isMonth1BtnActive = true)
                }
                BtnId.MONTH3_BTN -> {
                    setBtnActiveToFalse()
                    _chartState.value = _chartState.value.copy(isMonth3BtnActive = true)
                }
                BtnId.MONTH6_BTN -> {
                    setBtnActiveToFalse()
                    _chartState.value = _chartState.value.copy(isMonth6BtnActive = true)
                }
                BtnId.MONTH12_BTN -> {
                    setBtnActiveToFalse()
                    _chartState.value = _chartState.value.copy(isMonth12BtnActive = true)
                }
            }
        }
    }

    private fun setBtnActiveToFalse() {
        _chartState.value = _chartState.value.copy(isMonth1BtnActive = false)
        _chartState.value = _chartState.value.copy(isMonth3BtnActive = false)
        _chartState.value = _chartState.value.copy(isMonth6BtnActive = false)
        _chartState.value = _chartState.value.copy(isMonth12BtnActive = false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getStartDate(daysBefore: Long): String {
        // ISO_LOCAL_DATE formats date to uuuu-mm-dd
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val date: LocalDate = LocalDate.now().minusDays(daysBefore)
        val text: String = date.format(formatter)
        return text
    }

    fun assignId(slug: String) {
        this.slug = slug
        symbol = FixedCryptoList.getEnumName(slug)?.name.toString()
    }
}

enum class BtnId {
    MONTH1_BTN, MONTH3_BTN, MONTH6_BTN, MONTH12_BTN
}