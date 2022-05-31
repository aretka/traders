package com.example.traders.utils

import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.binance24hTickerData.PriceTickerData
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

fun roundAndFormatDouble(numToRound: Double, digitsRounded: Int = 2): String {
    return String.format("%,.${digitsRounded}f", numToRound)
}

fun BigDecimal.roundFormatBigDecimal(digitsRounded: Int = 2): String {
    val pattern = ",###.".plus(buildString {
        for (i in 1..digitsRounded) {
            append("0")
        }
    })
    val df = DecimalFormat(pattern)
    return df.format(this)
}

fun BigDecimal.roundNum(digitsRounded: Int = 2): BigDecimal {
    return this.setScale(digitsRounded, RoundingMode.UP)
}

// Rounds last number and returns PriceTickerData
fun returnTickerWithRoundedPrice(tickerData: PriceTickerData): PriceTickerData {
    val symbol = tickerData.symbol.replace("USDT", "")
    val numToRound = FixedCryptoList.valueOf(symbol).priceToRound
    val last = BigDecimal(tickerData.last).setScale(numToRound, RoundingMode.HALF_UP).toString()
    return tickerData.copy(last = last, symbol = symbol)
}