package com.example.traders.watchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.traders.R
import com.example.traders.databinding.ListItemCryptoBinding

class WatchListAdapter() : RecyclerView.Adapter<SimpleViewHolder<ListItemCryptoBinding>>() {
    private var list: List<CryptoInfo>  = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<ListItemCryptoBinding> {
        val layoutInflater = LayoutInflater.from(parent.context)
        print("VIEW HOLDEr CREATED")
        return SimpleViewHolder(ListItemCryptoBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: SimpleViewHolder<ListItemCryptoBinding>, position: Int) {
        val item = list[position]

        holder.binding.cryptoNameShortcut.text = item.name
        holder.binding.cryptoPrice.text = item.price.toString()
    }

    override fun getItemCount(): Int = list.size

    fun updateData(list: List<CryptoInfo>) {
        this.list = list
        this.notifyDataSetChanged()
    }
}

class SimpleViewHolder<T : ViewBinding>(val binding: T) : RecyclerView.ViewHolder(binding.root)