package com.example.traders.dialogs.depositDialog

import androidx.lifecycle.viewModelScope
import com.example.traders.BaseViewModel
import com.example.traders.database.Crypto
import com.example.traders.dialogs.DialogValidationMessage
import com.example.traders.repository.CryptoRepository
import com.example.traders.watchlist.allCrypto.singleCryptoScreen.priceStatisticsTab.Hilt_CryptoPriceStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val repository: CryptoRepository
) : BaseViewModel() {
    private val maxInputVal = BigDecimal(10000)
    private val minInputVal = BigDecimal(10)
    private val _state = MutableStateFlow(DepositState())
    val state = _state.asStateFlow()

    fun validateInput(input: String) {
        val decimalInput: BigDecimal
        if (input.isBlank()) {
            decimalInput = BigDecimal(0)
        } else {
            decimalInput = BigDecimal(input)
        }

        if (input.isBlank()) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                validationMessage = DialogValidationMessage.IS_EMPTY.message
            )
        } else if (decimalInput > maxInputVal) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                validationMessage = DialogValidationMessage.IS_TOO_HIGH.message
            )
        }  else if (decimalInput < minInputVal) {
            _state.value = _state.value.copy(
                isBtnEnabled = false,
                validationMessage = DialogValidationMessage.IS_TOO_LOW.message
            )
        } else {
            _state.value = _state.value.copy(
                isBtnEnabled = true,
                validationMessage = DialogValidationMessage.IS_VALID.message
            )
        }

        _state.value = _state.value.copy(currentInputVal = decimalInput)
    }

    fun updateBalance() {
        launch {
            val currBalance = repository.getCryptoBySymbol("USD") ?: Crypto(symbol = "USD")
            val newBalance = currBalance.amount + _state.value.currentInputVal

            repository.insertCrypto(currBalance.copy(amount = newBalance))
        }
    }
}