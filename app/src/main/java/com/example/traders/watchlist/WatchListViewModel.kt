package com.example.traders.watchlist

import com.example.traders.BaseViewModel
import com.example.traders.database.SortOrder
import com.example.traders.presentation.watchlist.WatchListState
import com.example.traders.utils.exhaustive
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        subscribeToCryptoPriceUpdates()
        startCryptoPricesPolling()
    }

    fun updateFavouritesList() {
        launch {
            watchListRepository.renewListWithFavourites()
        }
    }

    private fun subscribeToCryptoPriceUpdates() {
        launch {
            _state.update {
                it.copy(
                    showFavourites = watchListRepository.isFavouritesOn(),
                    sortOrder = watchListRepository.sortOrderType()
                )
            }
            watchListRepository.binanceCryptoList.collect { list ->
                _state.update {
                    it.copy(binanceCryptoData = list)
                }
            }
        }
    }

    private fun loadInitialCryptoList() {
        launchWithProgress {
            watchListRepository.refreshCryptoPrices()
        }
    }

    private fun startCryptoPricesPolling() {
        launch {
            watchListRepository.startCollectingBinanceTickerData()
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

    fun onSortNameButtonClicked() {
        when(_state.value.sortOrder) {
            SortOrder.BY_NAME_DESC -> updateSortOrder(SortOrder.BY_NAME_ASC)
            SortOrder.BY_NAME_ASC -> updateSortOrder(SortOrder.DEFAULT)
            else -> updateSortOrder(SortOrder.BY_NAME_DESC)
        }.exhaustive
    }

    fun onSortPriceChangeButtonClicked() {
        when(_state.value.sortOrder) {
            SortOrder.BY_CHANGE_DESC -> updateSortOrder(SortOrder.BY_CHANGE_ASC)
            SortOrder.BY_CHANGE_ASC -> updateSortOrder(SortOrder.DEFAULT)
            else -> updateSortOrder(SortOrder.BY_CHANGE_DESC)
        }.exhaustive
    }

    fun onScrolled() {
        _state.update { it.copy(shouldScrollTop = false) }
    }

    private fun updateSortOrder(newSortOrder: SortOrder) {
        launch {
            watchListRepository.saveSortOrderOnPreference(newSortOrder)
        }
        _state.update { it.copy(sortOrder = newSortOrder) }
        watchListRepository.sortList(newSortOrder)
    }

// This function cannot be called since connection hasnt been established yet at this point
// Subscribe and unsubscribe must be called when connection is successfully established and terminated respectively
//    private fun subscribeWebSocket() {
//        Log.e("ALlCryptoViewModel", "initWebSocket called")
//        webSocketClient.subscribe(listOf("btcusdt", "bnbusdt"), "ticker")
//    }
}
