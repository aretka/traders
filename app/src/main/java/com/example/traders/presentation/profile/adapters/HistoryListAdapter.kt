package com.example.traders.presentation.profile.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traders.R
import com.example.traders.database.Transaction
import com.example.traders.database.TransactionType
import com.example.traders.databinding.ListTransactionCryptoBinding
import com.example.traders.databinding.ListTransactionHeaderBinding
import com.example.traders.databinding.ListTransactionUsdBinding
import com.example.traders.database.FixedCryptoList

class HistoryListAdapter(val clearHistoryListener: () -> Unit) :
    ListAdapter<HistoryItem, RecyclerView.ViewHolder>(CryptoDiffCallback()) {

    fun addHeaderAndSubmitList(list: List<Transaction>) {
        val items = when (list) {
            null -> listOf(HistoryItem.Header)
            emptyList<Transaction>() -> listOf(HistoryItem.Header)
            else -> listOf(HistoryItem.Header) + list.map {
                if (it.symbol == "USD") {
                    HistoryItem.DepositItem(it)
                } else {
                    HistoryItem.BuySellItem(it)
                }
            }
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
            VIEW_TYPE_USD -> UsdViewHolder.from(parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is HistoryItem.Header -> VIEW_TYPE_HEADER
            is HistoryItem.BuySellItem -> VIEW_TYPE_CRYPTO
            is HistoryItem.DepositItem -> VIEW_TYPE_USD
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is CryptoViewHolder -> {
                val item = currentList[position] as HistoryItem.BuySellItem
                holder.bind(item.transaction)
            }
            is UsdViewHolder -> {
                val item = currentList[position] as HistoryItem.DepositItem
                holder.bind(item.transaction)
            }
            is HeaderViewHolder -> holder.bind(clearHistoryListener)
        }
    }

    class HeaderViewHolder(val binding: ListTransactionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(clearHistoryListener: () -> Unit) {
            binding.bin.setOnClickListener {
                clearHistoryListener()
            }
        }

        companion object {
            fun from(parent: ViewGroup): HeaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListTransactionHeaderBinding.inflate(layoutInflater, parent, false)
                return HeaderViewHolder(view)
            }
        }
    }

    class CryptoViewHolder(val binding: ListTransactionCryptoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.apply {
                date.text = item.time
                transactionTypeField.text = item.transactionType.message
                symbol.text = item.symbol
                slug.text = FixedCryptoList.valueOf(item.symbol).slug
                cryptoAmount.text = item.amount.toString()
                cryptoInUsd.apply {
                    when (item.transactionType) {
                        TransactionType.SELL -> {
                            text =
                                context.getString(R.string.usd_sign_plus, item.usdAmount.toString())
                            setTextColor(ContextCompat.getColor(context, R.color.green))
                        }
                        TransactionType.PURCHASE -> {
                            text = context.getString(
                                R.string.usd_sign_minus,
                                item.usdAmount.toString()
                            )
                            setTextColor(ContextCompat.getColor(context, R.color.red))
                        }
                    }
                }
                lastPrice.text = lastPrice.context.getString(R.string.usd_sign, item.lastPrice.toString())
                when (item.symbol) {
                    "USD" -> icon.setImageResource(R.drawable.ic_dollar)
                    else -> {
                        Glide.with(icon)
                            .load(FixedCryptoList.valueOf(item.symbol).logoUrl)
                            .placeholder(R.drawable.ic_download)
                            .error(R.drawable.ic_image_error)
                            .into(icon)
                    }
                }
            }

        }

        companion object {
            fun from(parent: ViewGroup): CryptoViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListTransactionCryptoBinding.inflate(layoutInflater, parent, false)
                return CryptoViewHolder(view)
            }
        }
    }

    class UsdViewHolder(val binding: ListTransactionUsdBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.apply {
                date.text = item.time
                symbol.text = item.symbol
                slug.text = FixedCryptoList.valueOf(item.symbol).slug
                amount.text = "+ " + item.amount.toString()
            }
        }

        companion object {
            fun from(parent: ViewGroup): UsdViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ListTransactionUsdBinding.inflate(layoutInflater, parent, false)
                return UsdViewHolder(view)
            }
        }
    }

    private class CryptoDiffCallback : DiffUtil.ItemCallback<HistoryItem>() {
        override fun areItemsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HistoryItem, newItem: HistoryItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private val VIEW_TYPE_HEADER = 0
        private val VIEW_TYPE_CRYPTO = 1
        private val VIEW_TYPE_USD = 2
    }
}

sealed class HistoryItem {
    abstract val id: Long

    data class BuySellItem(val transaction: Transaction) : HistoryItem() {
        override val id = transaction.id
    }

    data class DepositItem(val transaction: Transaction) : HistoryItem() {
        override val id = transaction.id
    }

    object Header : HistoryItem() {
        override val id = Long.MIN_VALUE
    }
}