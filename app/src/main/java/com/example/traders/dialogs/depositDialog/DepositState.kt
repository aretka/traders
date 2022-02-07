package com.example.traders.dialogs.depositDialog

import java.math.BigDecimal

data class DepositState(
    val validationMessage: String = "",
    val isBtnEnabled: Boolean = false,
    val currentInputVal: BigDecimal = BigDecimal(0)
)