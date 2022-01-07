package com.example.traders.watchlist.allCrypto

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.cryptoPriceData.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AllCryptoViewModel @Inject constructor(
    private val repository: CryptoRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow(AllCryptoState(emptyList(), emptyList()))
    val state = _state.asStateFlow()

    // I will transfer to StateFlow later, this is just for easier personal usage
    private val _cryptoData = MutableLiveData<List<Data>>()
    val cryptoData
        get() = _cryptoData

    private val _cryptoValues = MutableLiveData<List<String>?>(null)
    val cryptoValues
        get() = _cryptoValues

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing
        get() = _isRefreshing

    init {
        getCrypto()
    }

    fun getCrypto() {
        launchWithProgress {
            val response = try {
                repository.getCryptoPrices()
            } catch (e: IOException) {
                Log.d("Response", "IOException, internet connection interference: ${e}")
                return@launchWithProgress
            } catch (e: HttpException) {
                Log.d("Response", "HttpException, unexpected response: ${e}")
                return@launchWithProgress
            }

            if (response.isSuccessful && response.body() != null) {
                val responseData = response.body()
                _cryptoData.value = responseData?.data
                Log.d("Response", "Items: ${_cryptoData.value?.size}")
            } else {
                Log.d("Response", "Response not successful")
            }
        }
    }

    fun getCryptoOnRefresh() {
        launch {
            _isRefreshing.value = true
            val response = try {
                repository.getCryptoPrices()
            } catch (e: IOException) {
                Log.e("Response", "IOException, internet connection interference: ${e}")
                return@launch
            } catch (e: HttpException) {
                Log.e("Response", "HttpException, unexpected response: ${e}")
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                val responseData = response.body()
                _cryptoData.value = responseData?.data
            } else {
                Log.e("Response", "Response not successful")
            }
            _isRefreshing.value = false
        }
    }

    fun onCryptoClicked(id: String, symbol: String?) {
        if (symbol == null) return
        _cryptoValues.value = listOf(id, symbol)
    }

}
