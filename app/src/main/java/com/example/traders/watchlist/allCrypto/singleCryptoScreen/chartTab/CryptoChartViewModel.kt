package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CryptoChartViewModel @Inject constructor(
    private val repository: CryptoRepository
) : BaseViewModel() {
    private var id: String = ""
    private val _chartState = MutableStateFlow(ChartState())
    val chartState: StateFlow<ChartState>
        get() = _chartState

    fun fetchCryptoPriceStatistics(numDays: Long, candleInterval: String) {
        val startDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getStartDate(numDays)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        viewModelScope.launch {

            val response = try {
                repository.getCryptoChartData(id, startDate, candleInterval)
            } catch (e: IOException) {
                Log.d("Response", "IOException, internet connection interference: ${e}")
                return@launch
            } catch (e: HttpException) {
                Log.d("Response", "HttpException, unexpected response: ${e}")
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                val responseData = response.body()
                when (numDays.toInt()) {
                    90 -> _chartState.value =
                        _chartState.value.copy(chartDataFor90d = responseData!!.data.values)
                    360 -> _chartState.value =
                        _chartState.value.copy(chartDataFor360d = responseData!!.data.values)
                    else -> Log.e(this.javaClass.toString(), "Wrong num of days")
                }
            } else {
                Log.d("Response", "Response not successful")
            }

            if (_chartState.value.chartDataFor90d != null && _chartState.value.chartDataFor360d != null) {
                _chartState.value = _chartState.value.copy(chartBtnsEnabled = true)
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
        this.id = slug
    }
}

enum class BtnId {
    MONTH1_BTN, MONTH3_BTN, MONTH6_BTN, MONTH12_BTN
}