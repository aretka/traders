package com.example.traders.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteCrypto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val symbol: String
)
