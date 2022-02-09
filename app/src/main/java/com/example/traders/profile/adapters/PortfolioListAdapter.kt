package com.example.traders.profile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.profile.cryptoData.CryptoInUsd
import com.example.traders.watchlist.cryptoData.FixedCryptoList

class PortfolioListAdapter :
    ListAdapter<CryptoInUsd, CustomViewHolder<ListItemCryptoBinding>>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder<ListItemCryptoBinding> {
        return CustomViewHolder.from(parent)
    }

//    override fun getItemViewType(position: Int): Int {
//        return when(getItem(position)) {
//            is DataItem.Header -> VIEW_TYPE_HEADER
//            is DataItem.CryptoItem -> VIEW_TYPE_HEADER
//        }
//    }

    override fun onBindViewHolder(
        holder: CustomViewHolder<ListItemCryptoBinding>,
        position: Int
    ) {
        val item = currentList[position]
        holder.binding.cryptoNameShortcut.text = item.symbol
        holder.binding.cryptoFullName.text = FixedCryptoList.valueOf(item.symbol).slug
        holder.binding.cryptoPrice.text = item.amount.toString()
        holder.binding.cryptoPriceChange.text = holder.binding.cryptoPriceChange.context.getString(
            R.string.usd_sign,
            item.amountInUsd.toString()
        )
        Glide.with(holder.binding.cryptoLogo)
            .load(FixedCryptoList.valueOf(item.symbol).logoUrl)
            .placeholder(R.drawable.ic_download)
            .error(R.drawable.ic_image_error)
            .into(holder.binding.cryptoLogo)
    }

    private class DiffCallback : DiffUtil.ItemCallback<CryptoInUsd>() {
        override fun areItemsTheSame(oldItem: CryptoInUsd, newItem: CryptoInUsd) =
            oldItem.symbol == newItem.symbol

        override fun areContentsTheSame(oldItem: CryptoInUsd, newItem: CryptoInUsd) =
            oldItem == newItem
    }

    companion object {
        private val VIEW_TYPE_HEADER = 0
        private val VIEW_TYPE_CRYPTO = 1
    }
}

sealed class DataItem {
    abstract val id: String
    data class CryptoItem(val cryptoItem: CryptoInUsd): DataItem()      {
        override val id = cryptoItem.symbol
    }

    object Header: DataItem() {
        override val id = "Header"
    }
}

class CustomViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(parent: ViewGroup): CustomViewHolder<ListItemCryptoBinding> {
            val layoutInflater = LayoutInflater.from(parent.context)
            return CustomViewHolder(ListItemCryptoBinding.inflate(layoutInflater, parent, false))
        }
    }
}