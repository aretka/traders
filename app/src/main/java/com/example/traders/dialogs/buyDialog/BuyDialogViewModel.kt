package com.example.traders.dialogs.buyDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.dialogs.Constants
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BuyDialogViewModel @AssistedInject constructor(
    val repository: CryptoRepository,
    @Assisted val symbol: String,
    @Assisted val lastPrice: Double,
) : ViewModel() {
    // TODO update roundNumber() function to round to provided int which equals to numOfDigits after comma
    private val priceToRound = FixedCryptoList.valueOf(symbol).priceToRound
    private val amountToRound = FixedCryptoList.valueOf(symbol).amountToRound

    private val usdBalance: Double = getBalance()

    private val _state = MutableStateFlow(BuyState(usdBalance = usdBalance))
    val state = _state.asStateFlow()

    fun updateBalance() {
//         updates crypto balance
        repository.setStoredPrice(Constants.USD_BALANCE_KEY, _state.value.usdLeft.toFloat())
//         updates usd balance
        val newCryptoBalance = repository.getStoredTag(symbol) + _state.value.cryptoToGet.toFloat()
        repository.setStoredPrice(symbol, newCryptoBalance)
    }

    fun add1000UsdToBalance() {
        repository.setStoredPrice(Constants.USD_BALANCE_KEY, 1000F)
    }

    fun validateInput(enteredVal: String) {
        if (enteredVal.isBlank()) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_EMPTY
            )
        } else if (enteredVal.toDouble() > usdBalance) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_HIGH
            )
        } else if (enteredVal.toDouble() < _state.value.minInputVal) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_LOW
            )
        } else {
            _state.value = _state.value.copy(
                isBtnEnabled = true,
                messageType = DialogValidationMessage.IS_VALID
            )
        }

        if (enteredVal.isBlank()) {
            _state.value = _state.value.copy(inputVal = 0.0)
        } else {
            _state.value = _state.value.copy(inputVal = enteredVal.toDouble())
        }
        calculateNewBalance()
    }

    private fun calculateNewBalance() {
        var usdLeft = usdBalance
        var cryptoToGet = 0.0

        if (_state.value.messageType == DialogValidationMessage.IS_VALID) {
            usdLeft = usdBalance - _state.value.inputVal
            // TODO apply updated roundFunction() and provide numOfDigits(int) from fixedCryptoList by symbol to round
            cryptoToGet = _state.value.inputVal / lastPrice
        }

        _state.value = _state.value.copy(
            usdLeft = usdLeft,
            cryptoToGet = cryptoToGet
        )
    }

    private fun getBalance() = repository.getStoredTag(Constants.USD_BALANCE_KEY).toDouble()

    @AssistedFactory
    interface Factory {
        fun create(symbol: String, lastPrice: Double): BuyDialogViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            symbol: String,
            lastPrice: Double
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(symbol, lastPrice) as T
            }
        }
    }
}