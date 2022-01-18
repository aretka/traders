package com.example.traders

import android.widget.TextView
import androidx.core.content.ContextCompat

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