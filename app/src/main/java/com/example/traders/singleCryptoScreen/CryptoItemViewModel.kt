package com.example.traders.singleCryptoScreen

import androidx.lifecycle.SavedStateHandle
import com.example.traders.BaseViewModel
import com.example.traders.database.FavouriteCrypto
import com.example.traders.repository.CryptoRepository
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
    private val state: SavedStateHandle
) : BaseViewModel() {
    val symbol = state.get<String>("symbol")
    private var _isFavourite = state.get<Boolean>("isFavourite") ?: false
        set(value) {
            field = value
            state.set("isFavourite", value)
        }
    val isFavourite: Boolean
        get() = _isFavourite

    private val _isFavouriteBtnActive = MutableStateFlow(true)
    val isFavouriteBtnActive = _isFavouriteBtnActive.asStateFlow()

    private val _events = MutableSharedFlow<CryptoItemEvents>()
    val events = _events.asSharedFlow()

    fun onFavouriteBtnClicked(symbol: String) {
        launch {
            _isFavouriteBtnActive.value = false
            updateFavouritesInDb(symbol)
            _isFavourite = !_isFavourite
            delay(1500L)
            _isFavouriteBtnActive.value = true
        }
    }

    private suspend fun updateFavouritesInDb(symbol: String) {
        if(_isFavourite) {
            repository.deleteFavouriteCrypto(symbol)
            _events.emit(CryptoItemEvents.RemoveFromFavourites)
        } else {
            val favouriteCrypto = FavouriteCrypto(symbol = symbol)
            repository.insertFavouriteCrypto(favouriteCrypto)
            _events.emit(CryptoItemEvents.AddToFavourites)
        }
    }

}