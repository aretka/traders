package com.example.traders.presentation.cryptoDetailsScreen

import androidx.lifecycle.SavedStateHandle
import com.example.traders.presentation.BaseViewModel
import com.example.traders.database.FavouriteCrypto
import com.example.traders.network.repository.CryptoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CryptoItemViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val savedState: SavedStateHandle
) : BaseViewModel() {
    val symbol = savedState.get<String>("symbol")
//    private var _isFavourite = savedState.get<Boolean>("isFavourite") ?: false
//        set(value) {
//            field = value
//            savedState.set("isFavourite", value)
//        }
//    val isFavourite: Boolean
//        get() = _isFavourite

    private val _state = MutableStateFlow(CryptoItemState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<CryptoItemEvents>()
    val events = _events.asSharedFlow()

    init {
        fetchFavouriteFromDb(symbol)
    }

    fun onFavouriteBtnClicked(symbol: String) {
        launch {
            _state.value = _state.value.copy(isBtnActive = false)
            updateFavouritesInDb(symbol)
            _state.value = _state.value.copy(isFavourite = !_state.value.isFavourite)
            delay(1500L)
            _state.value = _state.value.copy(isBtnActive = true)
        }
    }

    fun fetchFavouriteFromDb(symbol: String?) {
        launch {
            symbol?.let {
                val favouriteCrypto: FavouriteCrypto? = repository.getFavouriteBySymbol(it)
                favouriteCrypto?.let {
                    _state.value = _state.value.copy(isFavourite = !_state.value.isFavourite)
                }
            }
        }
    }

    private suspend fun updateFavouritesInDb(symbol: String) {
        if(_state.value.isFavourite ) {
            repository.deleteFavouriteCrypto(symbol)
            _events.emit(CryptoItemEvents.RemoveFromFavourites)
        } else {
            val favouriteCrypto = FavouriteCrypto(symbol = symbol)
            repository.insertFavouriteCrypto(favouriteCrypto)
            _events.emit(CryptoItemEvents.AddToFavourites)
        }
    }

}