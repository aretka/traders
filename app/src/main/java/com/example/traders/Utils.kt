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

    if (percentagePriceChange.contains('-')) {
        textView.text = textView.context.getString(
            R.string.crypto_price_red,
            priceChange,
            percentagePriceChange
        )
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.red))
    } else {
        textView.text = textView.context.getString(
            R.string.crypto_price_green,
            priceChange,
            percentagePriceChange
        )
        textView.setTextColor(ContextCompat.getColor(textView.context, R.color.green))
    }

}