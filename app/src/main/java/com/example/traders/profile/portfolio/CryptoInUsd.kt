package com.example.traders.profile.portfolio

import java.math.BigDecimal

data class CryptoInUsd(
    val symbol: String,
    val amount: BigDecimal,
    val amountInUsd: BigDecimal
)
