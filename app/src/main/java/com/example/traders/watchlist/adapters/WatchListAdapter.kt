package com.example.traders.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.watchlist.cryptoData.Data

class WatchListAdapter() : RecyclerView.Adapter<SimpleViewHolder<ListItemCryptoBinding>>() {
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
            item.metrics.market_data.ohlcv_last_24_hour.open
                .minus(item.metrics.market_data.ohlcv_last_24_hour.close)
        )

        val percentagePriceChange = roundNumber(
            100 - item.metrics.market_data.ohlcv_last_24_hour.close
                .div(item.metrics.market_data.ohlcv_last_24_hour.open).times(100)
        )

        holder.binding.cryptoNameShortcut.text = item.symbol
        holder.binding.cryptoFullName.text = item.slug.replaceFirstChar { char -> char.uppercase() }
        holder.binding.cryptoPrice.text =
            roundNumber(item.metrics.market_data.ohlcv_last_24_hour.close)
        holder.binding.cryptoPriceChange.text = "${priceChange} + ${percentagePriceChange}%"
    }


    override fun getItemCount(): Int = list.size

    fun updateData(list: List<Data>) {
        this.list = list
        this.notifyDataSetChanged()
    }
}

class SimpleViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

fun roundNumber(numToRound: Double): String {
    return String.format("%.2f", numToRound)
}