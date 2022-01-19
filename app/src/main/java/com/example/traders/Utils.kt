package com.example.traders

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.binance24hTickerData.PriceTickerData
import com.google.gson.annotations.SerializedName
import kotlin.reflect.KClass

fun roundNumber(numToRound: Double): String {
    return String.format("%,.2f", numToRound)
}

fun getCryptoPriceChangeText(
    priceChange: String,
    percentagePriceChange: String,
    textView: TextView
) {
    var finalPriceChange = priceChange
    if (percentagePriceChange.contains('-')) {
        if(!priceChange.contains('-')) finalPriceChange = "-" + finalPriceChange
        textView.text = textView.context.getString(
            R.string.crypto_price_red,
            finalPriceChange,
            percentagePriceChange
        )
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red))
    } else {
        if(priceChange.contains('-')) finalPriceChange = finalPriceChange.replace("-", "")
        textView.text = textView.context.getString(
            R.string.crypto_price_green,
            finalPriceChange,
            percentagePriceChange
        )
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.green))
    }

}

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