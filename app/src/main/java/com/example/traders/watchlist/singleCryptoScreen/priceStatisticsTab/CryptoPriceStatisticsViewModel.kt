package com.example.traders.watchlist.singleCryptoScreen.priceStatisticsTab

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.cryptoStatsData.CryptoStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class CryptoPriceStatisticsViewModel @Inject constructor(
    private val repository: CryptoRepository
) : BaseViewModel() {
    private val _cryptoStatsResponse = MutableLiveData<CryptoStatistics>()
    val cryptoStatsResponse
        get() = _cryptoStatsResponse

    fun fetchCryptoPriceStatistics(slug: String) {
        viewModelScope.launch {

            var response = try {
                repository.getCryptoPriceStatistics(slug)
            } catch (e: IOException) {
                Log.e("ResponseCryptoItem", "IOException, internet connection interference: ${e}")
                return@launch
            } catch (e: HttpException) {
                Log.e("ResponseCryptoItem", "HttpException, unexpected response: ${e}")
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                val responseData = response.body()
                _cryptoStatsResponse.value = responseData!!
            } else {
                Log.e("ResponseCryptoItem", "Response not successful")
            }

        }
    }
}