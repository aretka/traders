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
    private var slug: String = ""
    private val _cryptoStatsResponse = MutableLiveData<CryptoChartData>()
    val cryptoStatsResponse
        get() = _cryptoStatsResponse

    private val _minVal = MutableLiveData<Float>()
    val minVal
        get() = _minVal

    private val _maxVal = MutableLiveData<Float>()
    val maxVal
        get() = _maxVal

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

    fun fetchCryptoPriceStatistics(numDays: Long = 30, candleInterval: String = "1d") {
        val startDate = getStartDate(numDays)
        viewModelScope.launch {

            val response = try {
                repository.getCryptoChartData(slug, startDate, candleInterval)
            } catch (e: IOException) {
                Log.d("Response", "IOException, internet connection interference: ${e}")
                return@launch
            } catch (e: HttpException) {
                Log.d("Response", "HttpException, unexpected response: ${e}")
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                val responseData = response.body()
                _cryptoStatsResponse.value = responseData!!
            } else {
                Log.d("Response", "Response not successful")
            }

        }
    }

    fun onChartBtnSelected(btnId: BtnId) {
        when(btnId) {
            BtnId.MONTH1_BTN -> {
                fetchCryptoPriceStatistics(30, "1d")
                _isMonth1BtnActive.value = true
                _isMonth3BtnActive.value = false
                _isMonth6BtnActive.value = false
                _isMonth12BtnActive.value = false
            }
            BtnId.MONTH3_BTN -> {
                fetchCryptoPriceStatistics(90,"1d")
                _isMonth1BtnActive.value = false
                _isMonth3BtnActive.value = true
                _isMonth6BtnActive.value = false
                _isMonth12BtnActive.value = false
            }
            BtnId.MONTH6_BTN -> {
                fetchCryptoPriceStatistics(180,"1w")
                _isMonth1BtnActive.value = false
                _isMonth3BtnActive.value = false
                _isMonth6BtnActive.value = true
                _isMonth12BtnActive.value = false
            }
            BtnId.MONTH12_BTN -> {
                fetchCryptoPriceStatistics(360,"1w")
                _isMonth1BtnActive.value = false
                _isMonth3BtnActive.value = false
                _isMonth6BtnActive.value = false
                _isMonth12BtnActive.value = true
            }
        }
    }

    private fun getStartDate(daysBefore: Long): String {
        // ISO_LOCAL_DATE formats date to uuuu-mm-dd
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val date: LocalDate = LocalDate.now().minusDays(daysBefore)
        val text: String = date.format(formatter)
        return text
    }

    fun findMinAndMaxValues() {
        _minVal.value = _cryptoStatsResponse.value?.data?.values?.minOf { it -> it[3] }
        _maxVal.value = _cryptoStatsResponse.value?.data?.values?.maxOf { it -> it[2] }
    }

    fun assignSlug(slug: String) {
        this.slug = slug
    }
}

enum class BtnId {
    MONTH1_BTN, MONTH3_BTN, MONTH6_BTN, MONTH12_BTN
}