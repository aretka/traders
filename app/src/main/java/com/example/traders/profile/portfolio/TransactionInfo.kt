package com.example.traders.profile.portfolio

import android.os.Parcelable
import com.example.traders.database.TransactionType
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionInfo(
    val symbol: String,
    val cryptoAmount: String,
    val usdAmount: String,
    val lastPrice: String,
    val transactionType: TransactionType,
    val newUsdBalance: String,
    val newCryptoBalance: String
) : Parcelable