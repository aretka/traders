package com.example.traders.di

import android.content.Context
import androidx.room.Room
import com.example.traders.database.Converters
import com.example.traders.database.CryptoDatabase
import com.example.traders.database.CryptoDatabaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideCryptoDatabaseDao(db: CryptoDatabase): CryptoDatabaseDao {
        return db.getDatabaseDao()
    }

    @Singleton
    @Provides
    fun provideConverters() = Converters()

    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext context: Context,
        convertersInstance: Converters
    ): CryptoDatabase {
        return Room.databaseBuilder(
            context,
            CryptoDatabase::class.java,
            "traders_database"
        ).fallbackToDestructiveMigration()
         .addTypeConverter(convertersInstance)
         .build()
    }
}