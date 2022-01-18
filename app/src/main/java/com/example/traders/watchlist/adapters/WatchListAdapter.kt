package com.example.traders.watchlist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.getCryptoPriceChangeText
import com.example.traders.roundNumber
import com.example.traders.watchlist.cryptoData.FixedCryptoList
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24Data
import com.example.traders.watchlist.cryptoData.binance24HourData.Binance24DataItem
import com.example.traders.watchlist.cryptoData.cryptoPriceData.Data
import kotlin.math.round

class WatchListAdapter(private val clickListener: SingleCryptoListener) :
    RecyclerView.Adapter<SimpleViewHolder<ListItemCryptoBinding>>() {
    private var list: List<Binance24DataItem> = emptyList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleViewHolder<ListItemCryptoBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)

        return SimpleViewHolder(ListItemCryptoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<ListItemCryptoBinding>, position: Int) {
        val item = list[position]
        holder.binding.cryptoPrice.text = roundNumber(item.lastPrice.toDouble())
        holder.binding.cryptoNameShortcut.text = item.symbol
        holder.binding.cryptoFullName.text = FixedCryptoList.valueOf(item.symbol.replace("USDT", "")).slug.replaceFirstChar { c -> c.uppercase() }
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

//        val priceChange = roundNumber(
//            item.metrics.market_data.ohlcv_last_24_hour.close - item.metrics.market_data.ohlcv_last_24_hour.open
//        )
//
//        val percentagePriceChange =
//            roundNumber(item.metrics.market_data.percent_change_usd_last_24_hours)
//
//        holder.binding.root.setOnClickListener {
//            clickListener.onClick(item)
//        }
//        holder.binding.cryptoNameShortcut.text = item.symbol
//        holder.binding.cryptoFullName.text = item.slug?.replaceFirstChar { char -> char.uppercase() }
//        holder.binding.cryptoPrice.text =
//            roundNumber(item.metrics.market_data.ohlcv_last_24_hour.close)
//        getCryptoPriceChangeText(
//            priceChange,
//            percentagePriceChange,
//            holder.binding.cryptoPriceChange
//        )
//
//        // Glide downloads and caches image in local storage for later usage
//        Glide.with(holder.binding.cryptoLogo)
//            .load("https://cryptologos.cc/logos/${item.slug}-${item.symbol?.lowercase()}-logo.png?v=014")
//            .placeholder(R.drawable.ic_download)
//            .error(R.drawable.ic_image_error)
//            .into(holder.binding.cryptoLogo)
    }

    override fun getItemCount(): Int = list.size

    fun updateData(list: List<Binance24DataItem>) {
        this.list = list
        this.notifyDataSetChanged()
    }
}

class SimpleViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)

class SingleCryptoListener(val clickListener: (id: String, symbol: String?) -> Unit) {
    fun onClick(data: Data) = clickListener(data.id, data.symbol)
}