package com.example.traders.dialogs.confirmationDialog

import com.example.traders.database.Transaction
import com.example.traders.profile.portfolio.TransactionInfo
import java.math.BigDecimal

sealed class ConfirmationType {
    object ResetBalance : ConfirmationType()
    object DeleteTransactionHistory : ConfirmationType()
    data class DepositUsd(val depositedAmount: BigDecimal) : ConfirmationType()
    data class BuySellCrypto(val transactionInfo: TransactionInfo) : ConfirmationType()
}