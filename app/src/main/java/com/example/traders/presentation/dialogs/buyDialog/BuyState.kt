package com.example.traders.presentation.dialogs.buyDialog

import com.example.traders.database.Crypto
import com.example.traders.presentation.dialogs.DialogValidationMessage
import java.math.BigDecimal

data class BuyState(
    val usdBalance: Crypto = Crypto(symbol = "USD"),
    val cryptoBalance: Crypto? = null,
    val inputVal: BigDecimal = BigDecimal(0.0),
    val minInputVal: BigDecimal = BigDecimal(10.0),
    val isBtnEnabled: Boolean = false,
    val cryptoToGet: BigDecimal = BigDecimal(0.0),
    val usdLeft: BigDecimal = BigDecimal(0.0),
    val messageType: DialogValidationMessage = DialogValidationMessage.IS_UNTOUCHED,
    val updateInput: Boolean = false,
    val validatedInputValue: String = ""
)
