package com.example.traders.presentation.dialogs.confirmationDialog

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.database.Crypto
import com.example.traders.database.TransactionType
import com.example.traders.network.repository.CryptoRepository
import com.example.traders.presentation.BaseViewModel
import com.example.traders.utils.DateUtils.getCurrentTime
import com.example.traders.utils.exhaustive
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal

class ConfirmationDialogViewModel @AssistedInject constructor(
    private val repository: CryptoRepository,
    @Assisted private val confirmationType: ConfirmationType
) : BaseViewModel() {

    private val _events = MutableSharedFlow<ConfirmationDialogEvent>()
    val events = _events.asSharedFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    fun onAcceptButtonClicked() {
        launch {
            when (confirmationType) {
                is ConfirmationType.ResetBalance -> deleteAllCryptoFromDb()
                is ConfirmationType.DeleteTransactionHistory -> deleteAllTransactions()
                is ConfirmationType.DepositUsd -> executeDepositTransactions()
                is ConfirmationType.BuySellCrypto -> executeBuySellTransactions()
            }.exhaustive
            _events.emit(ConfirmationDialogEvent.Dismiss)
        }
    }

    private suspend fun deleteAllCryptoFromDb() {
        repository.deleteAllCryptoFromDb()
    }

    private suspend fun deleteAllTransactions() {
        repository.deleteAllTransactions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun executeDepositTransactions() {
        val confirmationType = confirmationType as ConfirmationType.DepositUsd
        listOf(
            async { insertDepositTransaction(confirmationType.depositedAmount) },
            async { updateBalance(confirmationType.depositedAmount) }
        ).awaitAll()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun executeBuySellTransactions() {
        val confirmationType = confirmationType as ConfirmationType.BuySellCrypto
        listOf(
            async { insertBuySellTransaction(confirmationType) },
            async { updateUsdBalance(confirmationType.transactionInfo.newUsdBalance.toBigDecimal()) },
            async {
                updateCryptoBalance(
                    confirmationType.transactionInfo.symbol,
                    confirmationType.transactionInfo.newCryptoBalance.toBigDecimal()
                )
            }
        ).awaitAll()
    }

    private suspend fun updateUsdBalance(usdAmount: BigDecimal) {
        repository.updateCrypto(usdAmount, "USD")
    }

    //    Deletes crypto if cryptoAmount is 0
    private suspend fun updateCryptoBalance(symbol: String, cryptoAmount: BigDecimal) {
        val oldCrypto = repository.getCryptoBySymbol(symbol) ?: Crypto(symbol = symbol)
        if (cryptoAmount.compareTo(BigDecimal.ZERO) == 0) {
            repository.deleteCrypto(oldCrypto)
        } else {
            repository.insertCrypto(oldCrypto.copy(amount = cryptoAmount))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun insertDepositTransaction(depositedAmount: BigDecimal) {
        repository.insertTransaction(
            symbol = "USD",
            amount = depositedAmount,
            time = getCurrentTime(),
            transactionType = TransactionType.DEPOSIT
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun insertBuySellTransaction(confirmationType: ConfirmationType.BuySellCrypto) {
        repository.insertTransaction(
            symbol = confirmationType.transactionInfo.symbol,
            amount = confirmationType.transactionInfo.cryptoAmount.toBigDecimal(),
            usdAmount = confirmationType.transactionInfo.usdAmount.toBigDecimal(),
            lastPrice = confirmationType.transactionInfo.lastPrice.toBigDecimal(),
            time = getCurrentTime(),
            transactionType = confirmationType.transactionInfo.transactionType
        )
    }

    private suspend fun updateBalance(depositedAmount: BigDecimal) {
        val currBalance = repository.getCryptoBySymbol("USD") ?: Crypto(symbol = "USD")
        val newBalance = currBalance.amount + depositedAmount

        repository.insertCrypto(currBalance.copy(amount = newBalance))
    }

    fun onCancelButtonClicked() {
        launch {
            _events.emit(ConfirmationDialogEvent.Dismiss)
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(confirmationType: ConfirmationType): ConfirmationDialogViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: Factory,
            confirmationType: ConfirmationType
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(confirmationType) as T
            }
        }
    }
}