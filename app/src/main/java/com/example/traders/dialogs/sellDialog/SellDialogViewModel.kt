package com.example.traders.dialogs.sellDialog

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.repository.CryptoRepository
import com.example.traders.roundNum
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SellDialogViewModel @AssistedInject constructor(
    val repository: CryptoRepository,
    @Assisted val symbol: String,
    @Assisted val lastPrice: BigDecimal
) : BaseViewModel() {
    private val priceToRound = getPriceToRound()
    private val amountToRound = getAmountToRound()
    private val minCryptoToSell = getMinCryptoToSell()

    private val _state = MutableStateFlow(SellState(
        minInputVal = minCryptoToSell,
        priceToRound = priceToRound,
        amountToRound = amountToRound
    ))
    val state = _state.asStateFlow()

    init {
        getUsdBalance()
        getCryptoBalance()
        Log.e("TAG", "${_state.value.minInputVal}")
    }

    fun validateInput(enteredVal: String) {
        val decimalInputVal: BigDecimal

        if(enteredVal.isNotBlank()) {
            decimalInputVal = BigDecimal(enteredVal)
        } else {
            decimalInputVal = BigDecimal(0)
        }

        if (enteredVal.isBlank()) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_EMPTY
            )
        } else if (decimalInputVal > _state.value.cryptoBalance?.amount) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_HIGH
            )
        }  else if (decimalInputVal < _state.value.minInputVal) {
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

        if(enteredVal.isBlank()) {
            _state.value = _state.value.copy(inputVal = BigDecimal(0))
        } else {
            _state.value = _state.value.copy(inputVal = decimalInputVal)
        }

        calculateNewBalance()
    }

    fun updateBalance() {
        //Update Crypto balance (this was already calculated in calculateNewBalance())
        launch{
            _state.value.cryptoBalance?.let {
                if(_state.value.cryptoLeft.compareTo(BigDecimal.ZERO) == 0) {
                    Log.e("ROOM", "This crypto was deleted")
                    repository.deleteCrypto(it)
                } else {
                    repository.insertCrypto(it.copy(amount = _state.value.cryptoLeft))
                }
            }
        }

        //Update USD balance
        launch {
            val newUsdBalance = _state.value.usdBalance.amount + _state.value.usdToGet
            repository.insertCrypto(_state.value.usdBalance.copy(amount = newUsdBalance))
        }
    }

    private fun getPriceToRound() = FixedCryptoList.valueOf(symbol).priceToRound
    private fun getAmountToRound() = FixedCryptoList.valueOf(symbol).amountToRound
    private fun getMinCryptoToSell() = (BigDecimal(10).divide(lastPrice, amountToRound, BigDecimal.ROUND_HALF_UP))

    private fun getUsdBalance() {
        launch {
            val usdBalance = repository.getCryptoBySymbol("USD") ?: return@launch
            _state.value = _state.value.copy(usdBalance = usdBalance)
        }
    }

    private fun getCryptoBalance() {
        launch {
            val cryptoBalance = repository.getCryptoBySymbol(symbol) ?: Crypto(symbol = symbol)
            val amountInUsd = (lastPrice * cryptoBalance.amount).roundNum()
            _state.value = _state.value.copy(
                cryptoBalance = cryptoBalance,
                cryptoUsdBalance = amountInUsd,
                cryptoLeft = cryptoBalance.amount
            )
        }
    }


    private fun calculateNewBalance() {
        var cryptoLeft = _state.value.cryptoBalance?.amount ?: BigDecimal(0)
        var usdToGet = BigDecimal(0)
        if(_state.value.messageType == DialogValidationMessage.IS_VALID) {
            cryptoLeft -= _state.value.inputVal
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