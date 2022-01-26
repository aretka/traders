package com.example.traders.dialogs.sellDialog

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.roundNumber
import dagger.hilt.android.internal.Contexts.getApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SellDialogViewModel(
    private val symbol: String,
    private val lastPrice: Double,
    private val cryptoBalance: Double
): ViewModel() {
//    private val cryptoBalance = getApplication()?.getSharedPreferences()
    private val cryptoUsdBalance = roundNumber(lastPrice * cryptoBalance).toDouble()
    private val _state = MutableStateFlow(SellState(usdCryptoBalance = cryptoUsdBalance))
    val state = _state.asStateFlow()

    fun validateInput(enteredVal: String) {
        if (enteredVal.isBlank()) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_EMPTY
            )
        } else if (enteredVal.toDouble() > this.cryptoBalance) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_HIGH
            )
        }  else if (enteredVal.toDouble() < _state.value.minInputVal) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_LOW
            )
        } else if (enteredVal.toDouble() in _state.value.minInputVal..this.cryptoBalance) {
            _state.value = _state.value.copy(
                isBtnEnabled = true,
                messageType = DialogValidationMessage.IS_VALID
            )
        }
        if(enteredVal.isBlank()) {
            _state.value = _state.value.copy(inputVal = 0.0)
        } else {
            _state.value = _state.value.copy(inputVal = enteredVal.toDouble())
        }
        calculateNewBalance()
    }

    private fun calculateNewBalance() {
        var cryptoLeft = this.cryptoBalance
        var usdToGet = 0.0
        if(_state.value.messageType == DialogValidationMessage.IS_VALID) {
            cryptoLeft = this.cryptoBalance - _state.value.inputVal
            usdToGet = _state.value.inputVal * lastPrice
        }
        _state.value = _state.value.copy(
            cryptoLeft = cryptoLeft,
            usdToGet = usdToGet
        )
    }

    override fun onCleared() {
        Log.e("DialogViewModel", "onCleared called")
        super.onCleared()
    }
}