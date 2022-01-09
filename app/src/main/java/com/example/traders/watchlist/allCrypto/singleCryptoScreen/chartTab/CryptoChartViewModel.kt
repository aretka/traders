package com.example.traders.watchlist.allCrypto.singleCryptoScreen.chartTab

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.cryptoChartData.CryptoChartData
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _chartResponse = MutableLiveData(ChartData())
    val chartResponse
        get() = _chartResponse

    private val _isMonth1BtnActive = MutableLiveData(true)
    val isMonth1BtnActive
        get() = _isMonth1BtnActive

    private val _isMonth3BtnActive = MutableLiveData(false)
    val isMonth3BtnActive
        get() = _isMonth3BtnActive

    private val _isMonth6BtnActive = MutableLiveData(false)
    val isMonth6BtnActive
        get() = _isMonth6BtnActive

    private val _isMonth12BtnActive = MutableLiveData(false)
    val isMonth12BtnActive
        get() = _isMonth12BtnActive

    private val _chartBtnsEnabled = MutableLiveData(false)
    val chartBtnsEnabled
        get() = _chartBtnsEnabled

    fun fetchCryptoPriceStatistics(numDays: Long = 30, candleInterval: String = "1d") {
        val startDate = getStartDate(numDays)
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
                when(numDays.toInt()) {
                    90 -> _chartResponse.value = _chartResponse.value?.copy(chartDataFor90d = responseData!!.data.values)
                    360 -> _chartResponse.value = _chartResponse.value?.copy(chartDataFor360d = responseData!!.data.values)
                    else -> Log.e(this.javaClass.toString(), "Wrong num of days")
                }
            } else {
                Log.d("Response", "Response not successful")
            }

            if(_chartResponse.value?.chartDataFor90d != null && _chartResponse.value?.chartDataFor360d != null) {
                chartBtnsEnabled.value = true
            }
        }
    }

    fun fetchAllChartData() {
        fetchCryptoPriceStatistics(90, "1d")
        fetchCryptoPriceStatistics(360, "1w")
    }

    fun onChartBtnSelected(btnId: BtnId) {
        when(btnId) {
            BtnId.MONTH1_BTN -> {
                setBtnActiveToFalse()
                _isMonth1BtnActive.value = true
            }
            BtnId.MONTH3_BTN -> {
                setBtnActiveToFalse()
                _isMonth3BtnActive.value = true
            }
            BtnId.MONTH6_BTN -> {
                setBtnActiveToFalse()
                _isMonth6BtnActive.value = true
            }
            BtnId.MONTH12_BTN -> {
                setBtnActiveToFalse()
                _isMonth12BtnActive.value = true
            }
        }
    }

    private fun setBtnActiveToFalse() {
        _isMonth1BtnActive.value = false
        _isMonth3BtnActive.value = false
        _isMonth6BtnActive.value = false
        _isMonth12BtnActive.value = false
    }

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