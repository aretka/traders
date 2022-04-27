package com.example.traders.database

import androidx.lifecycle.LiveData
import androidx.room.*
import java.math.BigDecimal

@Dao
interface CryptoDatabaseDao {
    @Query("SELECT * FROM crypto")
    fun getAllCryptoLive(): LiveData<List<Crypto>>

    @Query("SELECT * FROM crypto")
    suspend fun getAllCrypto(): List<Crypto>

    @Query("SELECT * FROM crypto WHERE symbol=:symbol")
    suspend fun getCryptoBySymbol(symbol: String): Crypto?

    @Delete
    suspend fun deleteCrypto(crypto: Crypto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrypto(crypto: Crypto)

    @Query("DELETE FROM crypto")
    suspend fun deleteAllCryptoFromDb()

    @Query("UPDATE crypto SET amount=:amount WHERE  symbol=:symbol")
    suspend fun updateCrypto(amount: BigDecimal, symbol: String)

//    Transactions
    @Query("SELECT * FROM `transaction`")
    fun getAllTransactionsLive(): LiveData<List<Transaction>>

    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("DELETE FROM `transaction`")
    suspend fun deleteAllTransactions()

//    Favourite crypto
    @Query("SELECT * FROM favouritecrypto")
    fun getAllFavouritesLive() : LiveData<List<FavouriteCrypto>>

    @Query("SELECT * FROM favouritecrypto")
    suspend fun getAllFavourites() : List<FavouriteCrypto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteCrypto(favouriteCrypto: FavouriteCrypto)

    @Query("DELETE FROM favouritecrypto WHERE symbol=:symbol")
    suspend fun deleteFavouriteCrypto(symbol: String)

    @Query("SELECT * FROM favouritecrypto WHERE symbol=:symbol")
    suspend fun getFavouriteBySymbol(symbol: String): FavouriteCrypto?
}