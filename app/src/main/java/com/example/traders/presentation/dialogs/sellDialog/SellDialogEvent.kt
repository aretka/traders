package com.example.traders.presentation.dialogs.sellDialog

import com.example.traders.presentation.profile.portfolio.TransactionInfo

sealed class SellDialogEvent {
    data class Dismiss(val transactionInfo: TransactionInfo) : SellDialogEvent()
}
