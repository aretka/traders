package com.example.traders.dialogs.buyDialog

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
            listOf(
                async { saveTransactionToDb() },
                async { updateUSDBalance() },
                async { updateCryptoBalance() }
            ).awaitAll()

            _events.tryEmit(BuyDialogEvent.Dismiss)
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
            Log.e("OnBuy", "updateCryptoBalance called")
            val newCryptoBalance = _state.value.cryptoToGet + it.amount
            repository.insertCrypto(it.copy(amount = newCryptoBalance))
        }
    }

    fun onInputChanged(enteredVal: String) {
        validate(enteredVal)
        calculateNewBalance()
    }

    private fun validate(enteredVal: String) {
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
    }

    private fun getDecimalOfEnteredValue(enteredVal: String): BigDecimal {
        return if (enteredVal.isNotBlank()) {
            BigDecimal(enteredVal)
        } else {
            BigDecimal(0)
        }
    }

    private fun calculateNewBalance() {
        _state.value = with(_state.value) {
            var usdLeft = usdBalance.amount
            var cryptoToGet = BigDecimal(0)

            if (_state.value.messageType == DialogValidationMessage.IS_VALID) {
                usdLeft -= inputVal
                cryptoToGet = inputVal.divide(lastPrice, crypto.amountToRound, BigDecimal.ROUND_HALF_UP)
            }

            copy(
                usdLeft = usdLeft.roundNum(),
                cryptoToGet = cryptoToGet.roundNum(crypto.amountToRound)
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
            val cryptoBalance = repository.getCryptoBySymbol(crypto.name) ?: Crypto(symbol = crypto.name)
            _state.value = _state.value.copy(cryptoBalance = cryptoBalance)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTransaction(): Transaction {
        return Transaction(
            symbol = crypto.name,
            amount = _state.value.cryptoToGet,
            usdAmount = _state.value.inputVal,
            lastPrice = lastPrice,
            time = DateUtils.getCurrentTime(),
            transactionType = TransactionType.PURCHASE
        )
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
