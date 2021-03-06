package com.example.traders.presentation.dialogs.confirmationDialog

import com.example.traders.presentation.profile.portfolio.TransactionInfo
import java.math.BigDecimal

sealed class ConfirmationType {
    object ResetBalance : ConfirmationType()
    object DeleteTransactionHistory : ConfirmationType()
    data class DepositUsd(val depositedAmount: BigDecimal) : ConfirmationType()
    data class BuySellCrypto(val transactionInfo: TransactionInfo) : ConfirmationType()
}