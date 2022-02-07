package com.example.traders.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CryptoDatabaseDao {
    @Query("SELECT * FROM crypto")
    suspend fun getAllCrypto(): List<Crypto>

    @Query("SELECT * FROM crypto")
    fun getAllCryptoLive(): LiveData<List<Crypto>>

    @Query("SELECT * FROM crypto WHERE symbol=:symbol")
    suspend fun getCryptoBySymbol(symbol: String): Crypto?

    @Delete
    suspend fun deleteCrypto(crypto: Crypto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrypto(crypto: Crypto)

    @Query("DELETE FROM crypto")
    suspend fun deleteAllCryptoFromDb()
}