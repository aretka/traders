package com.example.traders.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CryptoDatabaseDao {
    @Query("SELECT * FROM crypto")
    suspend fun getAllCrypto(): List<Crypto>

    @Query("SELECT * FROM crypto WHERE symbol=:symbol")
    suspend fun getCryptoBySymbol(symbol: String): Crypto?

    @Insert
    suspend fun insertCrypto(vararg cryptoList: Crypto)
}