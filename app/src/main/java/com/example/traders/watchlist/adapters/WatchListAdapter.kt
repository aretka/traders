package com.example.traders.watchlist.adapters

import android.content.Context
import android.graphics.Color
import android.provider.Settings.Global.getString
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.watchlist.cryptoData.Data

class WatchListAdapter(val clickListener: SingleCryptoListener) :
    RecyclerView.Adapter<SimpleViewHolder<ListItemCryptoBinding>>() {
    private var list: List<Data> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleViewHolder<ListItemCryptoBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)

        return SimpleViewHolder(ListItemCryptoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<ListItemCryptoBinding>, position: Int) {
        val item = list[position]

        val priceChange = roundNumber(
            item.metrics.market_data.ohlcv_last_24_hour.close - item.metrics.market_data.ohlcv_last_24_hour.open
        )

        val percentagePriceChange = roundNumber(
            100 - item.metrics.market_data.ohlcv_last_24_hour.open
                .div(item.metrics.market_data.ohlcv_last_24_hour.close).times(100)
        )
        holder.binding.root.setOnClickListener {
            clickListener.onClick(item)
        }
        holder.binding.cryptoNameShortcut.text = item.symbol
        holder.binding.cryptoFullName.text = item.slug.replaceFirstChar { char -> char.uppercase() }
        holder.binding.cryptoPrice.text =
            roundNumber(item.metrics.market_data.ohlcv_last_24_hour.close)
        getCryptoPriceChangeText(
            priceChange,
            percentagePriceChange,
            holder.binding.cryptoPriceChange
        )

        // Glide downloads and caches image in local storage for later usage
        Glide.with(holder.binding.cryptoLogo)
            .load("https://cryptologos.cc/logos/${item.slug}-${item.symbol.lowercase()}-logo.png?v=014")
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_image_error)
            .into(holder.binding.cryptoLogo)
    }


    override fun getItemCount(): Int = list.size

    fun updateData(list: List<Data>) {
        this.list = list
        this.notifyDataSetChanged()
    }
}

class SimpleViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class SingleCryptoListener(val clickListener: (slug: String, symbol: String) -> Unit) {
    fun onClick(data: Data) = clickListener(data.slug, data.symbol)
}

fun roundNumber(numToRound: Double): String {
    return String.format("%.2f", numToRound)
}

fun getCryptoPriceChangeText(
    priceChange: String,
    percentagePriceChange: String,
    textView: TextView
) {

    if (priceChange.contains('-')) {
        textView.text = textView.context.getString(
            R.string.crypto_price_red,
            priceChange,
            percentagePriceChange
        )
        textView.setTextColor(Color.parseColor("#eb4034"))
    } else {
        textView.text = textView.context.getString(
            R.string.crypto_price_green,
            priceChange,
            percentagePriceChange
        )
        textView.setTextColor(Color.parseColor("#79e82e"))
    }

}