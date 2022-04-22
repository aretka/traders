package com.example.traders.dialogs.sellDialog

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
import com.example.traders.dialogs.validateChars
import com.example.traders.profile.portfolio.TransactionInfo
import com.example.traders.repository.CryptoRepository
import com.example.traders.utils.DateUtils
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
    private val repository: CryptoRepository,
    private val dialogValidation: DialogValidation,
    @Assisted val crypto: FixedCryptoList,
    @Assisted val lastPrice: BigDecimal
) : BaseViewModel() {
    private val minCryptoToSell = getMinCryptoToSell()

    private val _state = MutableStateFlow(
        SellState(
            minInputVal = minCryptoToSell
        )
    )
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<SellDialogEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        getUsdBalance()
        getCryptoBalance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSellButtonClicked() {
        launch {
            _events.emit(SellDialogEvent.Dismiss(
                TransactionInfo(
                    symbol = crypto.name,
                    cryptoAmount = _state.value.inputVal.toString(),
                    usdAmount = _state.value.usdToGet.toString(),
                    lastPrice = lastPrice.toString(),
                    transactionType = TransactionType.SELL,
                    newUsdBalance = getNewUsdBalance(),
                    newCryptoBalance = _state.value.cryptoLeft.toString()
                )
            ))
        }
    }

    fun onInputChanged(enteredVal: String) {
        val inputWithoutIlleagalChars = enteredVal.validateChars(crypto.amountToRound)
        if(inputWithoutIlleagalChars == enteredVal) {
            validate(enteredVal)
            calculateNewBalance()
        } else {
            _state.value = _state.value.copy(
                updateInput = true,
                validatedInputValue = inputWithoutIlleagalChars
            )
        }
    }

    fun inputUpdated() {
        _state.value = _state.value.copy(updateInput = false)
    }

    private fun validate(enteredVal: String): Boolean {
        val decimalEnteredVal = enteredVal.toBigDecimalOrNull()

        val messageType = dialogValidation.validate(
            decimalEnteredVal,
            _state.value.minInputVal,
            _state.value.cryptoBalance!!.amount
        )

        _state.value = _state.value.copy(
            inputVal = decimalEnteredVal ?: BigDecimal(0),
            isBtnEnabled = messageType == DialogValidationMessage.IS_VALID,
            messageType = messageType
        )

        return messageType == DialogValidationMessage.IS_VALID
    }

    private fun getNewUsdBalance(): String {
        return (_state.value.usdBalance.amount + _state.value.usdToGet).toString()
    }

    private fun getMinCryptoToSell() =
        (BigDecimal(10).divide(lastPrice, crypto.amountToRound, BigDecimal.ROUND_HALF_UP))

    private fun getUsdBalance() {
        launch {
            val usdBalance = repository.getCryptoBySymbol("USD") ?: return@launch
            _state.value = _state.value.copy(usdBalance = usdBalance)
        }
    }

    private fun getCryptoBalance() {
        launch {
            val cryptoBalance = repository.getCryptoBySymbol(crypto.name) ?: Crypto(symbol = crypto.name)
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
            cryptoLeft = cryptoLeft.roundNum(crypto.amountToRound),
            usdToGet = usdToGet.roundNum()
        )
    }

    @AssistedFactory
    interface Factory {
        fun create(crypto: FixedCryptoList, lastPrice: BigDecimal): SellDialogViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            crypto: FixedCryptoList,
            lastPrice: BigDecimal
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(crypto, lastPrice) as T
            }
        }
    }

}