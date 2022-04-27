package com.example.traders.watchlist

import com.example.traders.BaseViewModel
import com.example.traders.database.FilterPreferences
import com.example.traders.database.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchListViewModel @Inject constructor(
    private val watchListRepository: WatchListRepository
) : BaseViewModel() {

    private val _state = MutableStateFlow(WatchListState())
    val state = _state.asStateFlow()

    init {
        loadInitialCryptoList()
        launch {
            watchListRepository.binanceCryptoList.collect { list ->
                _state.update {
                    it.copy(
                        binanceCryptoData = list,
                        showFavourites = watchListRepository.isFavouritesOn()
                    )
                }
            }
        }
    }

    private fun loadInitialCryptoList() {
        launchWithProgress {
            watchListRepository.refreshCryptoPrices()
        }
    }

    fun getCryptoOnRefresh() {
        launch {
            _state.update { it.copy(isRefreshing = true) }
            watchListRepository.refreshCryptoPrices()
            _state.update { it.copy(isRefreshing = false) }
        }
    }

    fun onFavouriteButtonClicked() {
        launch {
            _state.update {
                val isFavouritesOn = !it.showFavourites
                watchListRepository.saveIsFavouriteOnPreference(isFavouritesOn)
                it.copy(showFavourites = isFavouritesOn)
            }
        }
    }

    private fun sortList(preferences: FilterPreferences) {
        when (preferences.sortOrder) {
            SortOrder.DEFAULT -> emitDefaultList()
            SortOrder.BY_NAME_ASC -> emitSortedByNameAsc()
            SortOrder.BY_NAME_DESC -> emitSortedByNameDesc()
            SortOrder.BY_CHANGE_ASC -> emitSortedByChangeAsc()
            SortOrder.BY_CHANGE_DESC -> emitSortedByChangeDesc()
        }
    }

    private fun emitDefaultList() {
//        TODO: sort by enum order
//        val sortedList = _state.value.binanceCryptoData.sortedBy { it.isFavourite }
//        _state.value = _state.value.copy(binanceCryptoData = sortedList)
    }

    private fun emitSortedByNameAsc() {
        _state.value =
            _state.value.copy(binanceCryptoData = _state.value.binanceCryptoData.sortedBy { it.symbol })
    }

    private fun emitSortedByNameDesc() {
        _state.value =
            _state.value.copy(binanceCryptoData = _state.value.binanceCryptoData.sortedByDescending { it.symbol })
    }

    private fun emitSortedByChangeAsc() {
        _state.value =
            _state.value.copy(binanceCryptoData = _state.value.binanceCryptoData.sortedBy { it.priceChangePercent })
    }

    private fun emitSortedByChangeDesc() {
        _state.value =
            _state.value.copy(binanceCryptoData = _state.value.binanceCryptoData.sortedByDescending { it.priceChangePercent })
    }

// This function cannot be called since connection hasnt been established yet at this point
// Subscribe and unsubscribe must be called when connection is successfully established and terminated respectively
//    private fun subscribeWebSocket() {
//        Log.e("ALlCryptoViewModel", "initWebSocket called")
//        webSocketClient.subscribe(listOf("btcusdt", "bnbusdt"), "ticker")
//    }
}
