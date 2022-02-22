package com.example.traders.dialogs.sellDialog

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.database.Transaction
import com.example.traders.database.TransactionType
import com.example.traders.dialogs.DialogValidation
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.utils.getCurrentTime
import com.example.traders.repository.CryptoRepository
import com.example.traders.utils.roundNum
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

class SellDialogViewModel @AssistedInject constructor(
    val repository: CryptoRepository,
    val dialogValidation: DialogValidation,
    @Assisted val symbol: String,
    @Assisted val lastPrice: BigDecimal
) : BaseViewModel() {
    private val priceToRound = getPriceToRound()
    private val amountToRound = getAmountToRound()
    private val minCryptoToSell = getMinCryptoToSell()

    private val _state = MutableStateFlow(
        SellState(
            minInputVal = minCryptoToSell,
            priceToRound = priceToRound,
            amountToRound = amountToRound
        )
    )
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<SellDialogEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        getUsdBalance()
        getCryptoBalance()
        Log.e("TAG", "${_state.value.minInputVal}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSellButtonClicked() {
        launch {
            listOf(
                async { saveTransactionToDb() },
                async { updateUsdBalance() },
                async { updateCryptoBalance() }
            ).awaitAll()

            Log.e("EVENTS", "events emitted")
            _events.emit(SellDialogEvent.Dismiss)
        }
    }

    fun onInputChanged(enteredVal: String) {
        validate(enteredVal)
        calculateNewBalance()

    }

    private fun validate(enteredVal: String): Boolean {
        val decimalInputVal = getDecimalFromString(enteredVal)

        val messageType = dialogValidation.validate(
            decimalInputVal,
            _state.value.minInputVal,
            _state.value.cryptoBalance!!.amount
        )

        _state.value = _state.value.copy(
            inputVal = decimalInputVal,
            isBtnEnabled = messageType == DialogValidationMessage.IS_VALID,
            messageType = messageType
        )

        return messageType == DialogValidationMessage.IS_VALID
    }

    private fun getDecimalFromString(enteredVal: String): BigDecimal {
        return if (enteredVal.isNotBlank()) {
            BigDecimal(enteredVal)
        } else {
            BigDecimal(0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun saveTransactionToDb() {
        repository.insertTransaction(createTransaction())
    }



    private suspend fun updateCryptoBalance() {
        _state.value.cryptoBalance?.let {
            if (_state.value.cryptoLeft.compareTo(BigDecimal.ZERO) == 0) {
                repository.deleteCrypto(it)
            } else {
                repository.insertCrypto(it.copy(amount = _state.value.cryptoLeft))
            }
        }
    }

    private suspend fun updateUsdBalance() {
        val newUsdBalance = _state.value.usdBalance.amount + _state.value.usdToGet
        repository.insertCrypto(_state.value.usdBalance.copy(amount = newUsdBalance))
    }

    private fun getPriceToRound() = FixedCryptoList.valueOf(symbol).priceToRound
    private fun getAmountToRound() = FixedCryptoList.valueOf(symbol).amountToRound
    private fun getMinCryptoToSell() =
        (BigDecimal(10).divide(lastPrice, amountToRound, BigDecimal.ROUND_HALF_UP))

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
        if (_state.value.messageType == DialogValidationMessage.IS_VALID) {
            cryptoLeft -= _state.value.inputVal
            usdToGet = _state.value.inputVal * lastPrice
        }
        _state.value = _state.value.copy(
            cryptoLeft = cryptoLeft.roundNum(_state.value.amountToRound),
            usdToGet = usdToGet.roundNum()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTransaction(): Transaction {
        return Transaction(
            symbol = symbol,
            amount = _state.value.inputVal,
            usdAmount = _state.value.usdToGet,
            lastPrice = lastPrice,
            time = getCurrentTime(),
            transactionType = TransactionType.SELL
        )
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