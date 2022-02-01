package com.example.traders.dialogs.sellDialog

import com.example.traders.dialogs.DialogValidationMessage
import java.math.BigDecimal

data class SellState(
    val priceToRound: Int = 2,
    val amountToRound: Int = 2,
    val cryptoBalance: BigDecimal = BigDecimal(0.0),
    val usdCryptoBalance: BigDecimal = BigDecimal(0.0),
    val inputVal: BigDecimal = BigDecimal(0.0),
    val minInputVal: BigDecimal = BigDecimal(0.01),
    val isBtnEnabled: Boolean = false,
    val cryptoLeft: BigDecimal = BigDecimal(0.0),
    val usdToGet: BigDecimal = BigDecimal(0.0),
    val messageType: DialogValidationMessage = DialogValidationMessage.IS_EMPTY
)
