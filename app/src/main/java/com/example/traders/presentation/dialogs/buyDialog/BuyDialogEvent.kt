package com.example.traders.presentation.dialogs.buyDialog

import com.example.traders.presentation.profile.portfolio.TransactionInfo

sealed class BuyDialogEvent {
    data class Dismiss(val transactionInfo: TransactionInfo): BuyDialogEvent()
}