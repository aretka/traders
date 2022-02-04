package com.example.traders.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Crypto::class],
    version = 3,
)
@TypeConverters(Converters::class)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun getDatabaseDao(): CryptoDatabaseDao
}