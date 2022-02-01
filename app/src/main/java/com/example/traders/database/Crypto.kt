package com.example.traders.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Crypto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val symbol: String,
    val amount: Double = 0.0
)