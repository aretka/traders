package com.example.traders.dialogs.sellDialog

import com.example.traders.dialogs.DialogValidationMessage

data class SellState(
    val priceToRound: Int = 2,
    val amountToRound: Int = 2,
    val cryptoBalance: Double = 0.0,
    val usdCryptoBalance: Double = 0.0,
    val inputVal: Double = 0.0,
    val minInputVal: Double = 0.01,
    val isBtnEnabled: Boolean = false,
    val cryptoLeft: Double = 0.0,
    val usdToGet: Double = 0.0,
    val messageType: DialogValidationMessage = DialogValidationMessage.IS_EMPTY
)
