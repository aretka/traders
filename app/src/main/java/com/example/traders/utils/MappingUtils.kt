package com.example.traders.utils

import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import java.math.BigDecimal
import kotlin.reflect.KClass

fun paramsToJson(params: List<String>, subscription: String, type: String): String {
    val paramString = params.joinToString(separator = ", ") { param ->
        "\"${param}@${type}\""
    }
    return "{\n" +
            "    \"method\": \"${subscription}\",\n" +
            "    \"params\": [ ${paramString}],\n" +
            "    \"id\": 1 \n" +
            "}"
}

// Converts PriceTickerData tp Binance24DataItem
fun PriceTickerData?.ToBinance24DataItem(): Binance24DataItem? {
    if (this == null) return null
    return Binance24DataItem(
        symbol = symbol,
        last = last,
        high = high,
        low = low,
        open = open,
        priceChange = priceChange,
        priceChangePercent = priceChangePercent
    )
}

// Converts enum names to String array
fun KClass<out Enum<*>>.enumConstantNames() =
    this.java.enumConstants.map(Enum<*>::name)

fun String.toBigDecimal(): BigDecimal {
    if (this.isBlank()) {
        return BigDecimal(0)
    } else {
        return BigDecimal(this)
    }
}
