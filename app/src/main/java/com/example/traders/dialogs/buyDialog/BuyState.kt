package com.example.traders.dialogs.buyDialog

import com.example.traders.dialogs.DialogValidationMessage
import java.math.BigDecimal

data class BuyState(
    val amountToRound: Int = 2,
    val priceNumToRound: Int = 2,
    val usdBalance: BigDecimal = BigDecimal(0.0),
    val inputVal: BigDecimal = BigDecimal(0.0),
    val minInputVal: BigDecimal = BigDecimal(10.0),
    val isBtnEnabled: Boolean = false,
    val cryptoToGet: BigDecimal = BigDecimal(0.0),
    val usdLeft: BigDecimal = BigDecimal(0.0),
    val messageType: DialogValidationMessage = DialogValidationMessage.IS_EMPTY
)