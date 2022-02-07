package com.example.traders.profile.cryptoData

import java.math.BigDecimal

data class CryptoInUsd(
    val symbol: String,
    val amount: BigDecimal,
    val amountInUsd: BigDecimal
)
