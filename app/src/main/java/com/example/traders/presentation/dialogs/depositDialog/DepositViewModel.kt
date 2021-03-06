package com.example.traders.presentation.dialogs.depositDialog

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.traders.presentation.BaseViewModel
import com.example.traders.presentation.dialogs.DialogValidation
import com.example.traders.presentation.dialogs.DialogValidationMessage
import com.example.traders.presentation.dialogs.validateChars
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class DepositViewModel @Inject constructor(
    private val dialogValidation: DialogValidation
) : BaseViewModel() {
    private val _state = MutableStateFlow(DepositState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<DepositDialogEvent>()
    val events = _events.asSharedFlow()

    fun onInputChanged(input: String) {
        val inputWithoutIlleagalChars = input.validateChars()
        if (inputWithoutIlleagalChars == input) {
            validate(input)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun onDepositButtonClicked() {
        launch {
            _events.emit(DepositDialogEvent.Dismiss(enteredAmount = _state.value.currentInputVal))
        }
    }

    private fun validate(enteredVal: String) {
        val decimalEnteredVal = enteredVal.toBigDecimalOrNull()

        val validationMessage = dialogValidation.validate(
            decimalEnteredVal,
            _state.value.minInputVal,
            _state.value.maxInputVal
        )

        _state.value = _state.value.copy(
            validationMessage = validationMessage,
            isBtnEnabled = validationMessage == DialogValidationMessage.IS_VALID,
            currentInputVal = decimalEnteredVal ?: BigDecimal(0)
        )
    }
}