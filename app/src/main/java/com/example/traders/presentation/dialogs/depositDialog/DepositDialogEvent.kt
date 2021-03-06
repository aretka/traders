package com.example.traders.presentation.dialogs.depositDialog

import com.example.traders.database.Transaction
import java.math.BigDecimal

sealed class DepositDialogEvent {
    data class Dismiss(val enteredAmount: BigDecimal) : DepositDialogEvent()
}