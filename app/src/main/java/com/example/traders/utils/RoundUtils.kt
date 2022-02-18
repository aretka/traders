package com.example.traders.utils

import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import java.math.BigDecimal
import java.math.RoundingMode

fun roundAndFormatDouble(numToRound: Double, digitsRounded: Int = 2): String {
    return String.format("%,.${digitsRounded}f", numToRound)
}

fun BigDecimal.roundNum(digitsRounded: Int = 2): BigDecimal {
    return this.setScale(digitsRounded, RoundingMode.UP)
}

// Rounds last number and returns PriceTickerData
fun returnTickerWithRoundedPrice(tickerData: PriceTickerData): PriceTickerData {
    val symbol = tickerData.symbol.replace("USDT", "")
    val numToRound = FixedCryptoList.valueOf(symbol).priceToRound
    val last = BigDecimal(tickerData.last).setScale(numToRound, RoundingMode.HALF_UP).toString()
    return tickerData.copy(last = last)
}