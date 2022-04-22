package com.example.traders.dialogs.buyDialog

import com.example.traders.profile.portfolio.TransactionInfo

sealed class BuyDialogEvent {
    data class Dismiss(val transactionInfo: TransactionInfo): BuyDialogEvent()
}
