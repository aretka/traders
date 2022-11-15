package com.example.traders.presentation.dialogs.depositDialog

import java.math.BigDecimal

sealed class DepositDialogEvent {
    data class Dismiss(val enteredAmount: BigDecimal) : DepositDialogEvent()
}
