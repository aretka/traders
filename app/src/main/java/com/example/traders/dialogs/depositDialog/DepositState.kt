package com.example.traders.dialogs.depositDialog

import java.math.BigDecimal

data class DepositState(
    val validationMessage: String = "",
    val isBtnEnabled: Boolean = false,
    val currentInputVal: BigDecimal = BigDecimal(0),
    val minInputVal: BigDecimal = BigDecimal(10),
    val maxInputVal: BigDecimal =  BigDecimal(10000),
)