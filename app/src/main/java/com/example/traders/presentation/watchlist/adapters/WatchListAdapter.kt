package com.example.traders.presentation.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.utils.roundAndFormatDouble
import com.example.traders.utils.setPriceChangeText
import com.example.traders.utils.setPriceChangeTextColor
import com.example.traders.database.FixedCryptoList
import com.example.traders.network.models.binance24HourData.BinanceDataItem

class WatchListAdapter(private val clickListener: SingleCryptoListener) :
    ListAdapter<BinanceDataItem, SimpleViewHolder<ListItemCryptoBinding>>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleViewHolder<ListItemCryptoBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)

        return SimpleViewHolder(ListItemCryptoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: SimpleViewHolder<ListItemCryptoBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        // changing only lastPrice, priceChange and priceChangePercent
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
//            changing only click listener if isFavourite changed
            val item = currentList[position]
            val condition = payloads[0] as Boolean

            if(!condition) {
                val priceRoundNum = FixedCryptoList.valueOf(item.symbol).priceToRound
                holder.binding.cryptoPrice.text =
                    roundAndFormatDouble(item.last.toDouble(), priceRoundNum)
                holder.binding.cryptoPriceChange.setPriceChangeText(
                    roundAndFormatDouble(item.priceChange.toDouble(), priceRoundNum),
                    roundAndFormatDouble(item.priceChangePercent.toDouble())
                )
                holder.binding.cryptoPriceChange.setPriceChangeTextColor()
            } else {
                val slug = FixedCryptoList.valueOf(item.symbol).slug
                holder.binding.root.setOnClickListener { clickListener.onClick(slug, item.symbol) }
            }
        }

    }

    override fun onBindViewHolder(holder: SimpleViewHolder<ListItemCryptoBinding>, position: Int) {
        val item = currentList[position]
        val slug = FixedCryptoList.valueOf(item.symbol).slug
        val priceRoundNum = FixedCryptoList.valueOf(item.symbol).priceToRound

        holder.binding.root.setOnClickListener { clickListener.onClick(slug, item.symbol) }
        holder.binding.cryptoPrice.text = roundAndFormatDouble(item.last.toDouble(), priceRoundNum)
        holder.binding.cryptoNameShortcut.text = item.symbol
        holder.binding.cryptoFullName.text = slug.replaceFirstChar { c -> c.uppercase() }
        holder.binding.cryptoPriceChange.setPriceChangeText(
            roundAndFormatDouble(item.priceChange.toDouble(), priceRoundNum),
            roundAndFormatDouble(item.priceChangePercent.toDouble())
        )
        holder.binding.cryptoPriceChange.setPriceChangeTextColor()

        Glide.with(holder.binding.cryptoLogo)
            .load(FixedCryptoList.valueOf(item.symbol).logoUrl)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_image_error)
            .into(holder.binding.cryptoLogo)
    }

    private class DiffCallback : DiffUtil.ItemCallback<BinanceDataItem>() {

        override fun areItemsTheSame(oldItem: BinanceDataItem, newItem: BinanceDataItem) =
            oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(oldItem: BinanceDataItem, newItem: BinanceDataItem) =
            oldItem == newItem

        override fun getChangePayload(
            oldItem: BinanceDataItem,
            newItem: BinanceDataItem
        ): Any {
            // this is called when symbols are the same but contents differ
//            passing true if isFavourite changed, because it means that only this field has changed
//            and false if the same
            return oldItem.isFavourite != newItem.isFavourite
        }
    }
}

class SimpleViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class SingleCryptoListener(val clickListener: (slug: String, symbol: String) -> Unit) {
    fun onClick(slug: String, symbol: String) = clickListener(slug, symbol)
}