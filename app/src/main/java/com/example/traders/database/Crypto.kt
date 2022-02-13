package com.example.traders.database

import androidx.room.*
import java.math.BigDecimal

@Entity
data class Crypto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "symbol")
    val symbol: String,

    @ColumnInfo(name = "amount")
    val amount: BigDecimal = BigDecimal(0),
)
