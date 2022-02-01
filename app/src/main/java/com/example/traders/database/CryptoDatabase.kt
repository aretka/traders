package com.example.traders.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Crypto::class],
    version = 1,
)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun getDatabaseDao(): CryptoDatabaseDao
}