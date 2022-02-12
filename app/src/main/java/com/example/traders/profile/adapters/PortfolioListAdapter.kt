package com.example.traders.profile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding
import com.example.traders.databinding.ListPortfolioHeaderBinding
import com.example.traders.profile.cryptoData.CryptoInUsd
import com.example.traders.watchlist.cryptoData.FixedCryptoList

class PortfolioListAdapter :
    ListAdapter<DataItem, RecyclerView.ViewHolder>(CryptoDiffCallback()) {

    fun addHeaderAndSubmitList(list: List<CryptoInUsd>) {
        val items = when (list) {
            null -> emptyList()
            emptyList<CryptoInUsd>() -> emptyList()
            else -> listOf(DataItem.Header) + list.map { DataItem.CryptoItem(it) }
        }
        submitList(items)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder.from(parent)
            VIEW_TYPE_CRYPTO -> CryptoViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DataItem.Header -> VIEW_TYPE_HEADER
            is DataItem.CryptoItem -> VIEW_TYPE_CRYPTO
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is CryptoViewHolder -> {
                val item = currentList[position] as DataItem.CryptoItem
                holder.bind(item.cryptoInUsd)
            }
        }
    }

    class HeaderViewHolder(val binding: ListPortfolioHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListPortfolioHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    class CryptoViewHolder(val binding: ListItemCryptoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CryptoInUsd) {
            binding.cryptoNameShortcut.text = item.symbol
            binding.cryptoFullName.text = FixedCryptoList.valueOf(item.symbol).slug
            binding.cryptoPrice.text = item.amount.toString()
            binding.cryptoPriceChange.text = binding.cryptoPriceChange.context.getString(
                R.string.usd_sign,
                item.amountInUsd.toString()
            )
            Glide.with(binding.cryptoLogo)
                .load(FixedCryptoList.valueOf(item.symbol).logoUrl)
                .placeholder(R.drawable.ic_download)
                .error(R.drawable.ic_image_error)
                .into(binding.cryptoLogo)
        }

        companion object {
            fun from(parent: ViewGroup): CryptoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListItemCryptoBinding.inflate(layoutInflater, parent, false)
                return CryptoViewHolder(view)
            }
        }
    }

    private class CryptoDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private val VIEW_TYPE_HEADER = 0
        private val VIEW_TYPE_CRYPTO = 1
    }
}

sealed class DataItem {
    abstract val id: String

    data class CryptoItem(val cryptoInUsd: CryptoInUsd) : DataItem() {
        override val id = cryptoInUsd.symbol
    }

    object Header : DataItem() {
        override val id = "Header"
    }
}