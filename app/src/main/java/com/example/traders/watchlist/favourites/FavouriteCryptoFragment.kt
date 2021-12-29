package com.example.traders.watchlist.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.traders.BaseFragment
import com.example.traders.databinding.FragmentTabFavouriteCryptoBinding

class FavouriteCryptoFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabFavouriteCryptoBinding.inflate(inflater, container, false)

        return binding.root
    }
}