package com.example.traders.database

enum class TransactionType(val message: String) {
    DEPOSIT("Deopsit"),
    PURCHASE("Purchase"),
    SELL("Sale")
}