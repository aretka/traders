package com.example.traders.watchlist.allCrypto

import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.cryptoPriceData.Data

data class AllCryptoState(
    val cryptoList: List<Data> = emptyList(),
    val binanceCryptoData: List<Binance24DataItem> = emptyList()
    )
