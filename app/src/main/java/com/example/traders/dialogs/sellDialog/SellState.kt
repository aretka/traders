package com.example.traders.dialogs.sellDialog

import com.example.traders.database.Crypto
import com.example.traders.dialogs.DialogValidationMessage
import java.math.BigDecimal

data class SellState(
    val priceToRound: Int = 2,
    val amountToRound: Int = 2,
    val cryptoBalance: Crypto? = null,
    val cryptoUsdBalance: BigDecimal? = null,
    val usdBalance: Crypto = Crypto(symbol = "USD"),
    val inputVal: BigDecimal = BigDecimal(0.0),
    val minInputVal: BigDecimal = BigDecimal(0.01),
    val isBtnEnabled: Boolean = false,
    val cryptoLeft: BigDecimal = BigDecimal(0.0),
    val usdToGet: BigDecimal = BigDecimal(0.0),
    val messageType: DialogValidationMessage = DialogValidationMessage.IS_UNTOUCHED,
    val updateInput: Boolean = false,
    val validatedInputValue: String = ""
)