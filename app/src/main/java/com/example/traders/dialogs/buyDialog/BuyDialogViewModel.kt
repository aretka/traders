package com.example.traders.dialogs.buyDialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.database.Transaction
import com.example.traders.database.TransactionType
import com.example.traders.dialogs.DialogValidation
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.getCurrentTime
import com.example.traders.repository.CryptoRepository
import com.example.traders.roundNum
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class BuyDialogViewModel @AssistedInject constructor(
    private val repository: CryptoRepository,
    private val dialogValidation: DialogValidation,
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

    private val _events = MutableSharedFlow<BuyDialogEvent>()
    val events = _events.asSharedFlow()

    init {
        getUsdBalance()
        getCryptoBalance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onBuyButtonClicked() {
        launch {
            listOf(
                async { saveTransactionToDb() },
                async { updateUSDBalance() },
                async { updateCryptoBalance() }
            ).awaitAll()

            _events.emit(BuyDialogEvent.Dismiss)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun saveTransactionToDb() {
        repository.insertTransaction(createTransaction())
    }

    private suspend fun updateUSDBalance() {
        repository.insertCrypto(_state.value.usdBalance.copy(amount = _state.value.usdLeft))
    }

    private suspend fun updateCryptoBalance() {
        _state.value.cryptoBalance?.let {
            val newCryptoBalance = _state.value.cryptoToGet + it.amount
            repository.insertCrypto(it.copy(amount = newCryptoBalance))
        }
    }

    fun onInputChanged(enteredVal: String) {
        if (validate(enteredVal)) {
            calculateNewBalance()
        }
    }

    private fun validate(enteredVal: String): Boolean {
        val decimalEnteredVal = getDecimalOfEnteredValue(enteredVal)

        val validationMessage = dialogValidation.validate(
            decimalEnteredVal,
            _state.value.minInputVal,
            _state.value.usdBalance.amount
        )

        _state.value = _state.value.copy(
            isBtnEnabled = validationMessage == DialogValidationMessage.IS_VALID,
            messageType = validationMessage,
            inputVal = decimalEnteredVal
        )

        return validationMessage == DialogValidationMessage.IS_VALID
    }

    private fun getDecimalOfEnteredValue(enteredVal: String): BigDecimal {
        return if (enteredVal.isNotBlank()) {
            BigDecimal(enteredVal)
        } else {
            BigDecimal(0)
        }
    }

    private fun getPriceToRound() = FixedCryptoList.valueOf(symbol).priceToRound
    private fun getAmountToRound() = FixedCryptoList.valueOf(symbol).amountToRound

    private fun calculateNewBalance() {
        _state.value = with(_state.value) {
            val usdLeft = usdBalance.amount - inputVal
            val cryptoToGet = inputVal.divide(lastPrice, amountToRound, BigDecimal.ROUND_HALF_UP)

            copy(
                usdLeft = usdLeft.roundNum(),
                cryptoToGet = cryptoToGet.roundNum(_state.value.amountToRound)
            )
        }
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
