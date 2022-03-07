package com.example.traders.dialogs.depositDialog

import com.example.traders.dialogs.DialogValidationMessage
import java.math.BigDecimal

data class DepositState(
    val validationMessage: DialogValidationMessage = DialogValidationMessage.IS_UNTOUCHED,
    val isBtnEnabled: Boolean = false,
    val currentInputVal: BigDecimal = BigDecimal(0),
    val minInputVal: BigDecimal = BigDecimal(10),
    val maxInputVal: BigDecimal = BigDecimal(10000),
    val updateInput: Boolean = false,
    val validatedInputValue: String = ""
)