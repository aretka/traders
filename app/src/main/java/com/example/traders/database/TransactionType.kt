package com.example.traders.database

enum class TransactionType(val message: String, val confirmationMessage: String = "") {
    DEPOSIT("Deposit"),
    PURCHASE("Purchase", "Are you sure you want to buy?"),
    SELL("Sale", "Are you sure you want to sell?")
}
