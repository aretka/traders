package com.example.traders.presentation.profile.history

import com.example.traders.presentation.BaseViewModel
import com.example.traders.network.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: CryptoRepository
): BaseViewModel() {
    val transactionList = repository.getAllTransactionsLive()

    fun clearHistory() : Unit {
        launch {
            repository.deleteAllTransactions()
        }
    }
}