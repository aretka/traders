package com.example.traders.utils

import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.traders.R

fun TextView.setPriceChangeText(
    priceChange: String,
    percentagePriceChange: String
) {
    var finalPriceChange = priceChange
    if (percentagePriceChange.contains('-')) {
        if (!priceChange.contains('-')) finalPriceChange = "-" + finalPriceChange
        text = context.getString(
            R.string.crypto_price_red,
            finalPriceChange,
            percentagePriceChange
        )
    } else {
        if (priceChange.contains('-')) finalPriceChange = finalPriceChange.replace("-", "")
        text = context.getString(
            R.string.crypto_price_green,
            finalPriceChange,
            percentagePriceChange
        )
    }

}

fun TextView.setPriceChangeTextColor() {
    if (this.text.contains('-')) {
        setTextColor(ContextCompat.getColor(context, R.color.red))
    } else {
        setTextColor(ContextCompat.getColor(context, R.color.green))
    }
}