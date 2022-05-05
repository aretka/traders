package com.example.traders.presentation.dialogs.buyDialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.database.TransactionType
import com.example.traders.presentation.dialogs.DialogValidation
import com.example.traders.presentation.dialogs.DialogValidationMessage
import com.example.traders.profile.portfolio.TransactionInfo
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.utils.roundNum
import com.example.traders.database.FixedCryptoList
import com.example.traders.presentation.dialogs.validateChars
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

private const val TAG = "BuyDialogViewModel"

class BuyDialogViewModel @AssistedInject constructor(
    private val repository: CryptoRepository,
    private val dialogValidation: DialogValidation,
    @Assisted val crypto: FixedCryptoList,
    @Assisted val lastPrice: BigDecimal
) : BaseViewModel() {

    private val _state = MutableStateFlow(BuyState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<BuyDialogEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        getUsdBalance()
        getCryptoBalance()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onBuyButtonClicked() {
        launch {
            _events.tryEmit(
                BuyDialogEvent.Dismiss(
                    TransactionInfo(
                        symbol = crypto.name,
                        cryptoAmount = _state.value.cryptoToGet.toString(),
                        usdAmount = _state.value.inputVal.toString(),
                        lastPrice = lastPrice.toString(),
                        transactionType = TransactionType.PURCHASE,
                        newUsdBalance = _state.value.usdLeft.toString(),
                        newCryptoBalance = getNewCryptoBalance()
                    )
                )
            )
        }
    }

    private fun getNewCryptoBalance(): String {
        return _state.value.cryptoBalance?.let {
            val newCryptoBalance = _state.value.cryptoToGet + it.amount
            newCryptoBalance.toString()
        } ?: "0"
    }

    fun onInputChanged(enteredVal: String) {
        val inputWithoutIlleagalChars = enteredVal.validateChars()
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

    private fun validate(enteredVal: String) {
        val decimalEnteredVal = enteredVal.toBigDecimalOrNull()

        val validationMessage = dialogValidation.validate(
            decimalEnteredVal,
            _state.value.minInputVal,
            _state.value.usdBalance.amount
        )
        _state.value = _state.value.copy(
            isBtnEnabled = validationMessage == DialogValidationMessage.IS_VALID,
            messageType = validationMessage,
            inputVal = decimalEnteredVal ?: BigDecimal(0)
        )
    }

    private fun calculateNewBalance() {
        _state.value = with(_state.value) {
            var usdLeft = usdBalance.amount
            var cryptoToGet = BigDecimal(0)

            if (_state.value.messageType == DialogValidationMessage.IS_VALID) {
                usdLeft -= inputVal
                cryptoToGet =
                    inputVal.divide(lastPrice, crypto.amountToRound, BigDecimal.ROUND_HALF_UP)
            }

            copy(
                usdLeft = usdLeft.roundNum(),
                cryptoToGet = cryptoToGet
            )
        }
    }

    private fun getUsdBalance() {
        launch {
            val usdBalance = repository.getCryptoBySymbol("USD") ?: return@launch
            _state.value = _state.value.copy(
                usdBalance = usdBalance,
                usdLeft = usdBalance.amount
            )
        }
    }

    private fun getCryptoBalance() {
        launch {
            val cryptoBalance =
                repository.getCryptoBySymbol(crypto.name) ?: Crypto(symbol = crypto.name)
            _state.value = _state.value.copy(cryptoBalance = cryptoBalance)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(crypto: FixedCryptoList, lastPrice: BigDecimal): BuyDialogViewModel
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
