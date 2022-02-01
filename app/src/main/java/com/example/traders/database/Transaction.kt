package com.example.traders.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val symbol: String,
    val amount: Double = 0.0,
    val lastPrice: Double? = null,
    val time: Date,
    val transactionType: String
)