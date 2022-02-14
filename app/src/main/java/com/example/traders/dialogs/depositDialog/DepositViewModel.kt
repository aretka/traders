package com.example.traders.dialogs.depositDialog

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.database.Transaction
import com.example.traders.database.TransactionType
import com.example.traders.dialogs.DialogValidation
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.repository.CryptoRepository
import com.example.traders.toBigDecimal
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val repository: CryptoRepository,
    private val dialogValidation: DialogValidation
) : BaseViewModel() {
    private val _state = MutableStateFlow(DepositState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<DepositDialogEvent>()
    val events = _events.asSharedFlow()

    fun onInputChanged(input: String) {
        validate(input)
    }

    private fun validate(enteredVal: String) {
        val decimalEnteredVal = enteredVal.toBigDecimal()

        val validationMessage = dialogValidation.validate(
            decimalEnteredVal,
            _state.value.minInputVal,
            _state.value.maxInputVal
        )

        _state.value = _state.value.copy(
            validationMessage = validationMessage.message,
            isBtnEnabled = validationMessage == DialogValidationMessage.IS_VALID,
            currentInputVal = decimalEnteredVal
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDepositButtonClicked() {
        launch {
            listOf(
                async { updateBalance() },
                async { saveTransactionToDb() }
            ).awaitAll()
            _events.emit(DepositDialogEvent.Dismiss)
        }
    }

    private suspend fun updateBalance() {
        val currBalance = repository.getCryptoBySymbol("USD") ?: Crypto(symbol = "USD")
        val newBalance = currBalance.amount + _state.value.currentInputVal

        repository.insertCrypto(currBalance.copy(amount = newBalance))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun saveTransactionToDb() {
        repository.insertTransaction(createTransaction())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createTransaction(): Transaction {
        return Transaction(
            symbol = "USD",
            amount = _state.value.currentInputVal,
            time = getCurrentTime(),
            transactionType = TransactionType.DEPOSIT
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentTime(): String {
        val date = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return date.format(formatter)
    }
}