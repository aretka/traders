package com.example.traders.profile.portfolio

import com.example.traders.profile.cryptoData.CryptoInUsd
import com.example.traders.profile.cryptoData.CryptoTicker
import com.github.mikephil.charting.data.PieEntry
import java.math.BigDecimal

data class PortfolioState(
    val colors: List<Int> = emptyList(),
    val chartData: List<PieEntry> = emptyList(),
    val cryptoListInUsd: List<CryptoInUsd> = emptyList(),
    val usdPricesFromBinance: List<CryptoTicker?> = emptyList(),
    val chartReadyForUpdate: Boolean = false,
    val totalPortfolioBalance: BigDecimal? = null
)