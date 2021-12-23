package com.example.traders.watchlist.allCrypto

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.traders.BaseViewModel
import com.example.traders.network.RetrofitInstance
import com.example.traders.watchlist.cryptoData.cryptoPriceData.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AllCryptoViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow(AllCryptoState(emptyList()))
    val state = _state.asStateFlow()

    // I will transfer to StateFlow later, this is just for easier personal usage
    private val _cryptoData = MutableLiveData<List<Data>>()
    val cryptoData
        get() = _cryptoData

    private val _cryptoValues = MutableLiveData<List<Any>>()
    val cryptoValues
        get() = _cryptoValues

    init {
        getCryptoPrices()
    }

    fun getCryptoPrices() {
        viewModelScope.launch {

            var response = try {
                RetrofitInstance.api.getCryptoPrices()
            } catch (e: IOException) {
                Log.d("Response", "IOException, internet connection interference: ${e}")
                return@launch
            } catch (e: HttpException) {
                Log.d("Response", "HttpException, unexpected response: ${e}")
                return@launch
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

    fun onCryptoClicked(slug: String, price: Float, priceChange: Float) {
        val list = listOf(slug, price, priceChange)
        _cryptoValues.value = list
    }

}
