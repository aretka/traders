package com.example.traders.database

enum class TransactionType(val message: String) {
    DEPOSIT("Deposit"),
    PURCHASE("Purchase"),
    SELL("Sale")
}
