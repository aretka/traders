package com.example.traders.presentation.profile.portfolio

import java.math.BigDecimal

data class CryptoInUsd(
    val symbol: String,
    val amount: BigDecimal,
    val amountInUsd: BigDecimal
)
