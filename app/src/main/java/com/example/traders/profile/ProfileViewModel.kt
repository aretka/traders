package com.example.traders.profile

import android.util.Log
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: CryptoRepository
): BaseViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    init {
        fetchAllCrypto()
    }

    private fun fetchAllCrypto() {
        launch {
            val response = repository.getAllCryptoPortfolio()
            val ada = repository.getCryptoBySymbol("ADA")
            val xrp = repository.getCryptoBySymbol("XRP") ?: "DOESNT EXIST"
            Log.e("TAG", "${ada}, ${xrp}, ${124.52.toLong()}")
            _state.value = _state.value.copy(cryptoPortfolio = response)
        }
    }

    private fun insertCrypto(crypto: Crypto) {
        launch {
            repository.insertCrypto(crypto)
        }
    }
}