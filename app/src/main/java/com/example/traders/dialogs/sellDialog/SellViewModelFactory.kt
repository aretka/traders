package com.example.traders.dialogs.sellDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SellViewModelFactory(
    private val symbol: String,
    private val lastPrice: Double,
    private val cryptoBalance: Double
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SellDialogViewModel::class.java)) {
            return SellDialogViewModel(symbol, lastPrice, cryptoBalance) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}