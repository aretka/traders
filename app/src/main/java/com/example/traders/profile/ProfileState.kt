package com.example.traders.profile

import com.example.traders.database.Crypto

data class ProfileState(
    val cryptoPortfolio: List<Crypto> = emptyList(),
    val top5PortfolioCoins: List<Crypto> = emptyList()
)