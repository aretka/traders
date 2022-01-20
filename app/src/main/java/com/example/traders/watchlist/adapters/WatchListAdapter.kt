package com.example.traders.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.getCryptoPriceChangeText
import com.example.traders.roundNumber
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.cryptoPriceData.Data

class WatchListAdapter(private val clickListener: SingleCryptoListener) :
    ListAdapter<Binance24DataItem, SimpleViewHolder<ListItemCryptoBinding>>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleViewHolder<ListItemCryptoBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)

        return SimpleViewHolder(ListItemCryptoBinding.inflate(layoutInflater, parent, false))
    }


    override fun onBindViewHolder(holder: SimpleViewHolder<ListItemCryptoBinding>, position: Int) {
        val item = currentList[position]
        val symbol = item.symbol.replace("USDT", "")
        val slug = FixedCryptoList.valueOf(symbol).slug

        holder.binding.root.setOnClickListener { clickListener.onClick(slug, symbol) }
        holder.binding.cryptoPrice.text = roundNumber(item.last.toDouble())
        holder.binding.cryptoNameShortcut.text = item.symbol
        holder.binding.cryptoFullName.text = slug.replaceFirstChar { c -> c.uppercase() }
        getCryptoPriceChangeText(
            roundNumber(item.priceChange.toDouble()),
            roundNumber(item.priceChangePercent.toDouble()),
            holder.binding.cryptoPriceChange
        )
        Glide.with(holder.binding.cryptoLogo)
            .load(FixedCryptoList.valueOf(item.symbol.replace("USDT", "")).logoUrl)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_image_error)
            .into(holder.binding.cryptoLogo)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Binance24DataItem>() {

        override fun areItemsTheSame(oldItem: Binance24DataItem, newItem: Binance24DataItem) =
            oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(oldItem: Binance24DataItem, newItem: Binance24DataItem) =
            oldItem == newItem
    }
}

class SimpleViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class SingleCryptoListener(val clickListener: (id: String, symbol: String?) -> Unit) {
    fun onClick(slug: String, symbol: String) = clickListener(slug, symbol)
}