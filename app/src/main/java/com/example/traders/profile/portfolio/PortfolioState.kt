package com.example.traders.profile.portfolio

import com.example.traders.database.Crypto
import com.example.traders.profile.cryptoData.CryptoInUsd
import com.example.traders.profile.cryptoData.CryptoTicker
import com.github.mikephil.charting.data.PieEntry
import java.math.BigDecimal

data class PortfolioState(
    val colors: List<Int> = emptyList(),
    val chartData: List<PieEntry> = emptyList(),
    val portfolioCryptoList: List<Crypto> = emptyList(),
    val cryptoListInUsd: List<CryptoInUsd> = emptyList(),
    val chartReadyForUpdate: Boolean = false,
    val portfolioInUsd: BigDecimal? = null,
    val isPortfolioEmpty: Boolean = false
)