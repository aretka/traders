package com.example.traders.watchlist.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.traders.databinding.FragmentTabFavouriteCryptoBinding

class FavouriteCryptoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentTabFavouriteCryptoBinding.inflate(inflater, container, false)

        return binding.root
    }
}