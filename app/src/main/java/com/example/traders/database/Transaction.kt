package com.example.traders.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val symbol: String,
    val amount: BigDecimal,
    val usdAmount: BigDecimal = BigDecimal(0),
    val lastPrice: BigDecimal = BigDecimal(0),
    val time: String,
    val transactionType: TransactionType
)
