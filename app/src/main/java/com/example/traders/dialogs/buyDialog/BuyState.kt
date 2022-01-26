package com.example.traders.dialogs.buyDialog

import com.example.traders.dialogs.DialogValidationMessage

data class BuyState(
    val usdBalance: Double = 0.0,
    val inputVal: Double = 0.0,
    val minInputVal: Double = 10.0,
    val isBtnEnabled: Boolean = false,
    val cryptoToGet: Double = 0.0,
    val usdLeft: Double = 0.0,
    val messageType: DialogValidationMessage = DialogValidationMessage.IS_EMPTY
)