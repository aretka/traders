package com.example.traders.dialogs.buyDialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.database.Transaction
import com.example.traders.database.TransactionType
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.getCurrentTime
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BuyDialogViewModel @AssistedInject constructor(
    val repository: CryptoRepository,
    @Assisted val symbol: String,
    @Assisted val lastPrice: BigDecimal
) : BaseViewModel() {
    private val priceToRound = getPriceToRound()
    private val amountToRound = getAmountToRound()

    private val _state = MutableStateFlow(
        BuyState(
            priceNumToRound = priceToRound,
            amountToRound = amountToRound
        )
    )
    val state = _state.asStateFlow()

    init {
        getUsdBalance()
        getCryptoBalance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveTransactionToDb() {
        launch {
            repository.insertTransaction(createTransaction())
        }
    }

    fun updateBalance() {
        //Update Crypto balance
        launch {
            _state.value.cryptoBalance?.let {
                val newCryptoBalance = _state.value.cryptoToGet + it.amount
                repository.insertCrypto(it.copy(amount = newCryptoBalance))
            }
        }

        //Update USD balance
        launch {
            repository.insertCrypto(_state.value.usdBalance.copy(amount = _state.value.usdLeft))
        }
    }

    fun validateInput(enteredVal: String) {
        val decimalEnteredVal: BigDecimal

        if (enteredVal.isNotBlank()) {
            decimalEnteredVal = BigDecimal(enteredVal)
        } else {
            decimalEnteredVal = BigDecimal(0)
        }

        if (enteredVal.isBlank()) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_EMPTY
            )
        } else if (decimalEnteredVal > _state.value.usdBalance.amount) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                messageType = DialogValidationMessage.IS_TOO_HIGH
            )
        } else if (decimalEnteredVal < _state.value.minInputVal) {
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

        // Input val is set to 0 if nothing entered
        _state.value = _state.value.copy(inputVal = decimalEnteredVal)

        calculateNewBalance()
    }

    private fun getPriceToRound() = FixedCryptoList.valueOf(symbol).priceToRound
    private fun getAmountToRound() = FixedCryptoList.valueOf(symbol).amountToRound

    private fun calculateNewBalance() {
        var usdLeft = _state.value.usdBalance.amount
        var cryptoToGet = BigDecimal(0.0)

        if (_state.value.messageType == DialogValidationMessage.IS_VALID) {
            usdLeft -= _state.value.inputVal
            // TODO apply updated roundFunction() and provide numOfDigits(int) from fixedCryptoList by symbol to round
            cryptoToGet =
                _state.value.inputVal.divide(lastPrice, amountToRound, BigDecimal.ROUND_HALF_UP)
        }

        _state.value = _state.value.copy(
            usdLeft = usdLeft.roundNum(),
            cryptoToGet = cryptoToGet.roundNum(_state.value.amountToRound)
        )
    }

    private fun getUsdBalance() {
        launch {
            val usdBalance = repository.getCryptoBySymbol("USD") ?: return@launch
            _state.value = _state.value.copy(usdBalance = usdBalance)
        }
    }

    private fun getCryptoBalance() {
        launch {
            val cryptoBalance = repository.getCryptoBySymbol(symbol) ?: Crypto(symbol = symbol)
            _state.value = _state.value.copy(cryptoBalance = cryptoBalance)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTransaction(): Transaction {
        return Transaction(
            symbol = symbol,
            amount = _state.value.cryptoToGet,
            usdAmount = _state.value.inputVal,
            lastPrice = lastPrice,
            time = getCurrentTime(),
            transactionType = TransactionType.PURCHASE
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(symbol: String, lastPrice: BigDecimal): BuyDialogViewModel
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