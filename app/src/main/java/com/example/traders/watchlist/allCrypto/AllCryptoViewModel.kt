package com.example.traders.watchlist.allCrypto

import com.example.traders.BaseViewModel
import com.example.traders.watchlist.CryptoInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AllCryptoViewModel @Inject constructor() : BaseViewModel() {
    private val _state = MutableStateFlow(AllCryptoState(emptyList()))
    val state = _state.asStateFlow()

    fun addItemsToList() {
        val cryptoItem = CryptoInfo("BTC/USD", "Bitcoin in dollars", 50000, 2091, 4.1)
        val list: MutableList<CryptoInfo> = mutableListOf()
        for (i in 1..20) {
            list.add(cryptoItem)
        }
        _state.value.cryptoList = list
    }
}
