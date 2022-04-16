package com.example.traders.dialogs.confirmationDialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.traders.BaseViewModel
import com.example.traders.dialogs.buyDialog.BuyDialogViewModel
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

    fun onAcceptButtonClicked() {
        launch {
            when (confirmationType) {
                ConfirmationType.RESET_BALANCE -> repository.deleteAllCryptoFromDb()
                ConfirmationType.DELETE_TRANSACTION_HISTORY -> repository.deleteAllTransactions()
            }
            _events.emit(ConfirmationDialogEvent.Dismiss)
        }
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