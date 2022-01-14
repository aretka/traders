package com.example.traders.watchlist.allCrypto

import androidx.lifecycle.MutableLiveData
import com.example.traders.BaseViewModel
import com.example.traders.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllCryptoViewModel @Inject constructor(
    private val repository: CryptoRepository
) : BaseViewModel() {
    private val _state = MutableStateFlow(AllCryptoState(emptyList()))
    val state = _state.asStateFlow()

    //TODO I will transfer to StateFlow later, this is just for easier personal usage
    private val _cryptoData = MutableLiveData<AllCryptoState>(AllCryptoState())
    val cryptoData
        get() = _cryptoData

    private val _isRefreshing = MutableLiveData(false)
    val isRefreshing
        get() = _isRefreshing

    init {
        getCrypto()
    }

    private fun getCrypto() {
        launchWithProgress {
            updateCryptoData()
        }
    }

    fun getCryptoOnRefresh() {
        launch {
            _isRefreshing.value = true
            updateCryptoData()
            _isRefreshing.value = false
        }
    }

    private suspend fun updateCryptoData() {
        val cryptoPrices = repository.getCryptoPrices().body()?.data ?: return
        _cryptoData.value = _cryptoData.value?.copy(cryptoList = cryptoPrices)
    }
}
