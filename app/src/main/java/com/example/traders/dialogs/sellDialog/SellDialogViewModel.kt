package com.example.traders.dialogs.sellDialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.dialogs.Constants
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.repository.CryptoRepository
import com.example.traders.roundNum
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal

class SellDialogViewModel @AssistedInject constructor(
    val repository: CryptoRepository,
    @Assisted val symbol: String,
    @Assisted val lastPrice: BigDecimal
) : ViewModel() {
    private val priceToRound = getPriceToRound()
    private val amountToRound = getAmountToRound()
    private val cryptoBalance = getCryptoBalance()
    private val cryptoUsdBalance = getUsdCryptoBalance()
    private val minCryptoToSell = getMinCryptoToSell()

    private val _state = MutableStateFlow(SellState(
        usdCryptoBalance = cryptoUsdBalance,
        cryptoBalance = cryptoBalance,
        cryptoLeft = cryptoBalance,
        minInputVal = minCryptoToSell,
        priceToRound = priceToRound,
        amountToRound = amountToRound
    ))
    val state = _state.asStateFlow()

    fun validateInput(enteredVal: String) {
        val decimalInputVal = BigDecimal(enteredVal)
        if (enteredVal.isBlank()) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_EMPTY
            )
        } else if (decimalInputVal > this.cryptoBalance) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_HIGH
            )
        }  else if (decimalInputVal < _state.value.minInputVal) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_LOW
            )
        } else if (decimalInputVal in _state.value.minInputVal..this.cryptoBalance) {
            _state.value = _state.value.copy(
                isBtnEnabled = true,
                messageType = DialogValidationMessage.IS_VALID
            )
        }
        if(enteredVal.isBlank()) {
            _state.value = _state.value.copy(inputVal = BigDecimal(0))
        } else {
            _state.value = _state.value.copy(inputVal = decimalInputVal)
        }
        calculateNewBalance()
    }

    fun updateBalance() {
//         updates crypto balance
        repository.setStoredPrice(symbol, _state.value.cryptoLeft.toFloat())

//         updates usd balance
        val newUsdBalance = repository.getStoredTag(Constants.USD_BALANCE_KEY) + _state.value.usdToGet.toFloat()
        repository.setStoredPrice(Constants.USD_BALANCE_KEY, newUsdBalance)
    }

    private fun getPriceToRound() = FixedCryptoList.valueOf(symbol).priceToRound
    private fun getAmountToRound() = FixedCryptoList.valueOf(symbol).amountToRound
    private fun getUsdCryptoBalance() = lastPrice * cryptoBalance
    private fun getCryptoBalance() = repository.getStoredTag(symbol).toBigDecimal().roundNum(amountToRound)
    private fun getMinCryptoToSell() = (BigDecimal(10) / lastPrice).roundNum(amountToRound)

    private fun calculateNewBalance() {
        var cryptoLeft = cryptoBalance
        var usdToGet = BigDecimal(0)
        if(_state.value.messageType == DialogValidationMessage.IS_VALID) {
            cryptoLeft = cryptoBalance - _state.value.inputVal
            usdToGet = _state.value.inputVal * lastPrice
        }
        _state.value = _state.value.copy(
            cryptoLeft = cryptoLeft.roundNum(_state.value.amountToRound),
            usdToGet = usdToGet.roundNum()
        )
    }

    override fun onCleared() {
        Log.e("DialogViewModel", "onCleared called")
        super.onCleared()
    }

    @AssistedFactory
    interface Factory {
        fun create(symbol: String, lastPrice: BigDecimal): SellDialogViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            symbol: String,
            lastPrice: BigDecimal
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(symbol, lastPrice) as T
            }
        }
    }

}